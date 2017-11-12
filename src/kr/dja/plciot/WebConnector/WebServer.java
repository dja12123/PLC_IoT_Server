/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package kr.dja.plciot.WebConnector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class WebServer implements IWebSocketRawTextObserver, IWebSocketReceiveObservable
{
	private static final int PORT = 8080;
	public static final String SEPARATOR = "/";

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Channel channel;

	private final Map<String, IWebSocketObserver> observers;
	private final Map<Channel, IWebSocketObserver> observerChannelMap;
	
	public WebServer()
	{
		this.observers = Collections.synchronizedMap(new HashMap<String, IWebSocketObserver>());
		this.observerChannelMap = Collections.synchronizedMap(new HashMap<Channel, IWebSocketObserver>());
	}
	
	@Override
	public void rawMessageReceive(Channel ch, String str)
	{
		PLC_IoT_Core.CONS.push("웹소켓 요청: " + str);
		
		String[] kv = str.split(SEPARATOR, 2);
		String key = kv[0];
		String value = null;

		IWebSocketObserver observer = this.observers.getOrDefault(key, null);
		
		if(observer == null)
		{
			return;
		}
		
		if(kv.length > 1)
		{
			value = kv[1];
		}
		
		
		this.observerChannelMap.put(ch, observer);
		observer.messageReceive(ch, key, value);
		
	}
	
	@Override
	public void rawChannelDisconnect(Channel ch)
	{
		IWebSocketObserver observer = this.observerChannelMap.getOrDefault(ch, null);
		if(observer == null)
		{
			return;
		}
		observer.channelDisconnect(ch);
		this.observerChannelMap.remove(ch);
	}
	
	@Override
	public void addObserver(String key, IWebSocketObserver o)
	{
		if(this.observers.containsKey(key))
		{
			new Exception("Duplicated Put Packet Observer").printStackTrace();
			return;
			
		}
		this.observers.put(key, o);
	}

	@Override
	public void deleteObserver(String key)
	{
		if(!this.observers.containsKey(key))
		{
			new Exception("Remove Packet Observer").printStackTrace();
			return;
		}
		IWebSocketObserver observer = this.observers.get(key);
		for(Channel ch : this.observerChannelMap.keySet())
		{
			if(this.observerChannelMap.get(ch) == observer)
			{
				this.observerChannelMap.remove(ch);
			}
		}
		this.observers.remove(key);
	}
	
	public static class WebServerBuilder implements IMultiThreadTaskCallback
	{
		private final WebServer instance;
		private Thread taskThread;
		
		public WebServerBuilder(WebServer instance)
		{
			this.instance = instance;
		}
		
		private void startTask(NextTask nextTask)
		{
			PLC_IoT_Core.CONS.push("웹 통신 관리자 로드 시작.");
			this.instance.bossGroup = new NioEventLoopGroup(1);
			this.instance.workerGroup = new NioEventLoopGroup();
			try
			{
				ServerBootstrap b = new ServerBootstrap();
				b.option(ChannelOption.SO_BACKLOG, 1024);
				b.group(this.instance.bossGroup, this.instance.workerGroup).channel(NioServerSocketChannel.class)
						.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new HTTPInitializer(this.instance));
				
				this.instance.channel = b.bind(PORT).sync().channel();
				
				PLC_IoT_Core.CONS.push("웹 통신 관리자 활성화.");
				nextTask.nextTask();
				this.instance.channel.closeFuture().sync();
				
			}
			catch (InterruptedException e)
			{
				nextTask.error(e, "WEB SERVER ERROR");
			}
		}
		
		private void shutDownTask(NextTask nextTask)
		{
			PLC_IoT_Core.CONS.push("웹 통신 관리자 종료 시작.");
			this.instance.channel.close();
			this.instance.bossGroup.shutdownGracefully();
			this.instance.workerGroup.shutdownGracefully();
			try
			{
				this.taskThread.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			PLC_IoT_Core.CONS.push("웹 통신 관리자 종료 성공.");
			nextTask.nextTask();
		}

		@Override
		public void executeTask(TaskOption option, NextTask nextTask)
		{
			if(option == TaskOption.START)
			{
				this.taskThread = new Thread(()->{this.startTask(nextTask);});
				this.taskThread.start();
			}
			else if(option == TaskOption.SHUTDOWN)
			{
				this.shutDownTask(nextTask);
			}
		}
		
	}
}