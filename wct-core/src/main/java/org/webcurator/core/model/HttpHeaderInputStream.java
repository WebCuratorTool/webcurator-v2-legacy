/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.core.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class HttpHeaderInputStream extends PushbackInputStream {
	
	public HttpHeaderInputStream(InputStream parent) {
		super(parent);
	}
	
	public String readLine() throws IOException {
		StringBuffer buff = new StringBuffer();
		
		int byteRead = -1;
		
		for( byteRead = read(); ; byteRead =read()) {
			// Handle EOF
			if( byteRead == -1) {
				return null;
			}
			
			// EOL with linefeed
			if( byteRead == 10) {
				break;
			}
			
			// EOL with carriage return
			if( byteRead == 13) { 
				int xbyte = read();
				if(xbyte != 10) {
					unread(xbyte);
				}
				break;
			}
			
			buff.append((char)byteRead);
		}
		return buff.toString();
	}
	
	
	public int skipHttpHeaders() throws IOException {
		LastBytes lastBytes = new LastBytes();
		int byteRead = -1;
		int numBytesRead = 0;
		
		//byteRead = read();
		while( (byteRead = read()) != -1) {
			numBytesRead++;
			lastBytes.push(byteRead);
			
			if( lastBytes.isTwoNewlines()) {
				return numBytesRead;
			}
		}
		return numBytesRead;
		
	}
	
	
	private static class LastBytes {
		int[] last = new int[4];
		
		public void push(int b) {
			last[0] = last[1];
			last[1] = last[2];
			last[2] = last[3];
			last[3] = b;
		}
		
		public boolean isTwoNewlines() {
			return 
				last[0] == 13 && last[1] == 10 && last[2] == 13 && last[3] == 10 ||
				last[2] == 10 && last[3] == 10 ||
				last[2] == 13 && last[3] == 13;
		}
		
	}	

}