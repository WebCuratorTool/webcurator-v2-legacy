/**
 * nz.govt.natlib.ndha.wctdpsdepositor - Software License
 *
 * Copyright 2007/2009 National Library of New Zealand.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * or the file "LICENSE.txt" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package nz.govt.natlib.ndha.wctdpsdepositor.mets;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class MetsWriterFactoryTest {

    @Test
    public void test_message_returns_instance() {
        MetsWriterFactory factory = new MetsWriterFactoryImpl();
        MetsWriter metsWriter = factory.createMetsWriter();

        assertThat(metsWriter, is(notNullValue()));
    }

    @Test
    public void test_same_instance_returned_on_multiple_calls() {
        MetsWriterFactory factory = new MetsWriterFactoryImpl();

        MetsWriter firstMetsWriter = factory.createMetsWriter();
        MetsWriter secondMetsWriter = factory.createMetsWriter();

        assertThat(firstMetsWriter, is(equalTo(secondMetsWriter)));
    }

}
