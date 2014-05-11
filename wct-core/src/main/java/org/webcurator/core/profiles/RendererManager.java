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
package org.webcurator.core.profiles;

import org.archive.crawler.framework.Processor;
import org.archive.crawler.settings.ComplexType;
import org.archive.crawler.settings.ListType;
import org.archive.crawler.settings.MapType;
import org.archive.crawler.settings.TextField;
import org.webcurator.ui.profiles.renderers.BooleanRenderer;
import org.webcurator.ui.profiles.renderers.ComplexTypeRenderer;
import org.webcurator.ui.profiles.renderers.ListTypeRenderer;
import org.webcurator.ui.profiles.renderers.MapRenderer;
import org.webcurator.ui.profiles.renderers.OptionRenderer;
import org.webcurator.ui.profiles.renderers.ProcessorMapRenderer;
import org.webcurator.ui.profiles.renderers.Renderer;
import org.webcurator.ui.profiles.renderers.ScopeRenderer;
import org.webcurator.ui.profiles.renderers.SimpleMapRenderer;
import org.webcurator.ui.profiles.renderers.TextFieldRenderer;
import org.webcurator.ui.profiles.renderers.TextRenderer;

/**
 * The <code>RendererManager</code> is responsible for working out which
 * UI renderers should be used to render the profile screens. This is very
 * tightly integrated with the Heritrix Profile.
 * 
 * @author bbeaumont
 *
 */
public class RendererManager {
	
	/**
	 * Get the appropriate renderer for the given element.
	 * @param anElement The element to get the renderer for.
	 * @return The appropriate renderer.
	 */
	public static Renderer getRenderer(ProfileElement anElement) {
		Object value = anElement.getValue();
		
		if( "/crawl-order/scope".equals(anElement.getAbsoluteName())) {
			return new ScopeRenderer();
		}
		else if( value instanceof Boolean) { 
			return new BooleanRenderer();
		}
		else if( value instanceof MapType) {
			Class contentType = ((MapType)value).getContentType();
            
			boolean simpleMap = contentType == String.class
								|| contentType == Integer.class
								|| contentType == Double.class
								|| contentType == Float.class
								|| contentType == Boolean.class;
			
			if(simpleMap) {
				return new SimpleMapRenderer();
			}
			else if(contentType != Processor.class) {
				return new MapRenderer();
			}
			else {
				return new ProcessorMapRenderer();
			}
		}
		else if( value instanceof ComplexType) {
			return new ComplexTypeRenderer();
		}

		else if( value instanceof TextField ) {
			return new TextFieldRenderer();
		}
		else if( anElement instanceof SimpleProfileElement &&
				 ((SimpleProfileElement) anElement).hasOptions()) {
			return new OptionRenderer();
		}
		else if( value instanceof ListType) {
			return new ListTypeRenderer();
		}
		else {
			return new TextRenderer();
		}
	}
}
