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

package nz.govt.natlib.ndha.wctdpsdepositor;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.webcurator.core.archive.dps.DpsDepositFacade;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Creates an instance of DpsDeposit Facade with all dependencies set.
 */
public class DpsDepositProxy implements DpsDepositFacade {

    public DepositResult deposit(Map<String, String> parameters, List<File> fileList) {
        DpsDepositFacade dpsDeposit = createInstance();
        return dpsDeposit.deposit(parameters, fileList);
    }

    public String loginToPDS(Map<String, String> parameters) throws RuntimeException {
        DpsDepositFacade dpsDeposit = createInstance();
        return dpsDeposit.loginToPDS(parameters);
    }

    private DpsDepositFacade createInstance() {
        Injector dependencyInjector = Guice.createInjector();
        return dependencyInjector.getInstance(DpsDepositFacadeImpl.class);
    }

}
