package kr.dja.plciot.UI;

import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import kr.dja.plciot.Log.Console;
import kr.dja.plciot.Log.ConsoleMessage;

public class ConsoleUI extends JScrollPane implements Observer
{
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("[yy/MM/dd HH:mm:ss.SS] ");
	private static final int LINE_MAX_LENGTH = 500;
	
	private final Console console;
	private JTextArea textArea;
	
	private Object synchObj;
	
	public ConsoleUI(Console console)
	{
		this.console = console;
		
		this.textArea = new JTextArea();
		this.textArea.setLineWrap(true);
		this.textArea.setEditable(false);
		
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.setViewportView(this.textArea);
		
		this.synchObj = new Object();
		
		this.console.addObserver(this);
	}

	@Override
	public void update(Observable console, Object messageObj)
	{
		ConsoleMessage message = (ConsoleMessage)messageObj;
		
		String textMessage = " [" + message.getIndex() + "] ";
		textMessage += TIME_FORMAT.format(message.getCreatedTime());
		textMessage += message.getMessage() + "\n";
		
		synchronized(this.synchObj)
		{
			// 텍스트 추가.
			this.textArea.append(textMessage);
			
			// textArea의 최대 라인 개수에서 초과된 라인 잘라내기.
			int numLinesToTrunk = this.textArea.getLineCount() - LINE_MAX_LENGTH;
			if(numLinesToTrunk > 0)
			{
				try
				{
					this.textArea.replaceRange("", 0, this.textArea.getLineEndOffset(numLinesToTrunk - 1));
				}
				catch (BadLocationException ex)
				{
		            ex.printStackTrace();
		        }
			}
			
			// 스크롤 바 위치 조정.
			this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
		}
	}

	public void shutdown()
	{
		this.console.deleteObserver(this);
		
	}
}
