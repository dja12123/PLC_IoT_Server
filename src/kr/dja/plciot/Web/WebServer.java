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
package kr.dja.plciot.Web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class WebServer
{
	private static final int PORT = 8080;

	public static void main(String[] args)
	{
		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try
		{
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new HTTPInitializer());
			
			Channel ch = b.bind(PORT).sync().channel();
			System.out.println("OPEN");
			
			ch.closeFuture().sync();
			
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	public static class WebServerBuilderManager implements IMultiThreadTaskCallback
	{
		private final WebServer instance;
		
		private EventLoopGroup bossGroup;
		private EventLoopGroup workerGroup;
		private Channel channel;
		
		public WebServerBuilderManager(WebServer instance)
		{
			this.instance = instance;
		}
		
		private void startTask(NextTask nextTask)
		{
			this.bossGroup = new NioEventLoopGroup(1);
			this.workerGroup = new NioEventLoopGroup();
			try
			{
				ServerBootstrap b = new ServerBootstrap();
				b.option(ChannelOption.SO_BACKLOG, 1024);
				b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
						.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new HTTPInitializer());
				
				this.channel = b.bind(PORT).sync().channel();
				System.out.println("OPEN");
				
				nextTask.nextTask();
				this.channel.closeFuture().sync();
				
			}
			catch (InterruptedException e)
			{
				nextTask.error(e, "WEB SERVER ERROR");
			}
			nextTask.nextTask();
			
		}
		
		private void shutDownTask(NextTask nextTask)
		{
			this.channel.close();
			this.bossGroup.shutdownGracefully();
			this.workerGroup.shutdownGracefully();
			nextTask.nextTask();
		}

		@Override
		public void executeTask(TaskOption option, NextTask nextTask)
		{
			if(option == TaskOption.START)
			{
				this.startTask(nextTask);
			}
			else if(option == TaskOption.SHUTDOWN)
			{
				this.shutDownTask(nextTask);
			}
		
		}
		
	}
}