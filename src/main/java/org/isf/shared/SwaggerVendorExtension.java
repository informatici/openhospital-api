/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.shared;

import java.util.ArrayList;
import java.util.List;

import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

//@Component
//@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class SwaggerVendorExtension implements OperationBuilderPlugin {

    @Override
    public void apply(OperationContext operationContext) {

        ObjectVendorExtension extension = new ObjectVendorExtension("x-codeSamples");
        extension.addProperty(new VendorExtension() {
            @Override
            public String getName() {
                return "lang";
            }

            @Override
            public Object getValue() {
                return "java";
            }
        });

        List<VendorExtension> extensions = new ArrayList<VendorExtension>();
        extensions.add(extension);
        operationContext.operationBuilder().extensions(extensions);
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return SwaggerPluginSupport.pluginDoesApply(documentationType);
    }
}
