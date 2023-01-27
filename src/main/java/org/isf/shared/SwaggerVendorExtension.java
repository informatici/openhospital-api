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
