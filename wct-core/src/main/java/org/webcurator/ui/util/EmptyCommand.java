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
package org.webcurator.ui.util;

/**
 * A default command object used for the profile controller or any other
 * controller that does not really need a command. 
 * 
 * Uses of this are probably rare. The Profile Controller needs it because
 * it parses the HttpServletRequest parameters directly, since the parameters
 * are dynamic. Most controllers/handlers should have a specific command 
 * class.
 * 
 * @author bbeaumont
 */
public class EmptyCommand {

}
