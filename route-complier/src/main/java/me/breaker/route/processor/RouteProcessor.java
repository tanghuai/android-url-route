package me.breaker.route.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import me.breaker.route.annotation.Autowired;
import me.breaker.route.annotation.Route;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("me.breaker.route.annotation.Route")
public class RouteProcessor extends AbstractProcessor {

    public static final String PACKAGE_OF_GENERATE_FILE = "me.breaker.route";

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if (set != null && !set.isEmpty()) {
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
            if (routeElements != null && !routeElements.isEmpty()) {

                ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(String.class));


                FieldSpec fieldSpec = FieldSpec.builder(inputMapTypeOfGroup, "mPathMap")
                        .addModifiers(PUBLIC, STATIC)
                        .initializer("new $T<>()", HashMap.class)
                        .build();

                MethodSpec.Builder initMethodBuilder = MethodSpec.methodBuilder("init")
                        .addModifiers(PUBLIC, STATIC);

                for (Element element : routeElements) {
                    Route route = element.getAnnotation(Route.class);
                    TypeMirror tm = element.asType();
                    initMethodBuilder.addStatement("mPathMap.put($S,$S)", route.value(), tm.toString());

                    ParameterSpec injectParamSpec = ParameterSpec.builder(TypeName.OBJECT, "target").build();

                    MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                            .addModifiers(PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(injectParamSpec);
                    injectMethodBuilder.addStatement("$T substitute = ($T)target", ClassName.get(tm), ClassName.get(tm));


                    for (Element field : element.getEnclosedElements()) {
                        Autowired routeParam = field.getAnnotation(Autowired.class);

                        if (routeParam != null) {
                            injectMethodBuilder.addStatement("substitute." + field.getSimpleName() + " = substitute.getIntent().getData().getQueryParameter($S)", field.getSimpleName());
                        }
                    }

                    try {
                        String packageName = tm.toString().substring(0, tm.toString().lastIndexOf("."));
                        String fileName = element.getSimpleName() + "$$Autowired";
                        JavaFile.builder(packageName,
                                TypeSpec.classBuilder(fileName)
                                        .addModifiers(PUBLIC)
                                        .addMethod(injectMethodBuilder.build())
                                        .build()
                        ).build().writeTo(filer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                try {
                    JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                            TypeSpec.classBuilder("PathConfig$$Route")
                                    .addModifiers(PUBLIC)
                                    .addField(fieldSpec)
                                    .addMethod(initMethodBuilder.build())
                                    .build()
                    ).build().writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

}
