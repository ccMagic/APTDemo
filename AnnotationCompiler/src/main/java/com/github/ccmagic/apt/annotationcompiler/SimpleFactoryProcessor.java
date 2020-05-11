package com.github.ccmagic.apt.annotationcompiler;

import com.github.ccmagic.apt.annotation.IShape;
import com.github.ccmagic.apt.annotation.SimpleFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


/**
 * 也就是我们在使用注解处理器的时候
 * 需要手动添加META-INF/services/javax.annotation.processing.Processor，
 */
public class SimpleFactoryProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Filer mFiler;
    private Elements mElementUtils;
    private Types mTypeUtils;
    private String packageStr = null;
    private int r;

    /**
     * 初始化处理器，方法中有一个ProcessingEnvironment类型的参数，ProcessingEnvironment是一个注解处理工具的集合。它包含了众多工具类。例如：
     * Filer可以用来编写新文件；
     * Messager可以用来打印错误信息；
     * Elements是一个可以处理Element的工具类。
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mTypeUtils = processingEnv.getTypeUtils();
    }

    /**
     * 返回值是一个Set集合，
     * 集合中指要处理的注解类型的名称(这里必须是完整的包名+类名，例如com.example.annotation.Factory)
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> stringSet = new HashSet<>();
        mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->getSupportedAnnotationTypes--> SimpleFactory.class.getCanonicalName: " + SimpleFactory.class.getCanonicalName());
        stringSet.add(SimpleFactory.class.getCanonicalName());
        return stringSet;
    }

    /**
     * 返回值表示注解是否由当前Processor 处理。
     * 如果返回 true，则这些注解由此注解来处理，后续其它的 Processor 无需再处理它们；
     * 如果返回 false，则这些注解未在此Processor中处理并，那么后续 Processor 可以继续处理它们
     * <p>
     * Element是一个接口，表示一个程序元素，它可以指代包、类、方法或者一个变量。Element已知的子接口有如下几种：
     * </P>
     * PackageElement    表示一个包程序元素。提供对有关包及其成员的信息的访问。
     * ExecutableElement 表示某个类或接口的方法、构造方法或初始化程序（静态或实例），包括注释类型元素。
     * TypeElement       表示一个类或接口程序元素。提供对有关类型及其成员的信息的访问。注意，枚举类型是一种类，而注解类型是一种接口。
     * VariableElement   表示一个字段、enum 常量、方法或构造方法参数、局部变量或异常参数。
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> start(" + r + ") >>>>>>>>>>>>>>>>>>>>>>>");
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SimpleFactory.class);
        if (!elements.isEmpty()) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> SimpleFactory.class.getSimpleName    : " + SimpleFactory.class.getSimpleName());
            mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> SimpleFactory.class.getName          : " + SimpleFactory.class.getName());
            mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> SimpleFactory.class.getCanonicalName : " + SimpleFactory.class.getCanonicalName());
            mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> IShape.class.getSimpleName           : " + IShape.class.getSimpleName());
            mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> IShape.class.getName                 : " + IShape.class.getName());
            mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> IShape.class.getCanonicalName        : " + IShape.class.getCanonicalName());

            mMessager.printMessage(Diagnostic.Kind.NOTE, "\nSimpleFactoryProcessor-->process--> start loop");
            mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> elements.size: " + elements.size());
            elements.forEach(element -> {
                if (element.getKind() != ElementKind.CLASS) {
                    throw new IllegalArgumentException("Only classes can be annotated with @" + SimpleFactory.class.getSimpleName());
                } else {
                    mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> element.getKind() == ElementKind.CLASS");
                }
                TypeElement typeElement = (TypeElement) element;
                if (packageStr == null) {
                    String qualifiedName = typeElement.getQualifiedName().toString();
                    mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> packageStr--> qualifiedName :" + qualifiedName);
                    packageStr = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
                    mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> packageStr--> packageStr    :" + packageStr);
                }

                SimpleFactory simpleFactory = typeElement.getAnnotation(SimpleFactory.class);
                mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> Annotation simpleFactory.name(): " + simpleFactory.name());
                mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> Annotation simpleFactory.classType(): " + simpleFactory.classType());
                //SimpleFactory注解必须配置Name
                if (simpleFactory.name().length() == 0) {
                    throw new IllegalArgumentException("id() in @" + SimpleFactory.class.getSimpleName() + " for class " + typeElement.getQualifiedName().toString() + " is null or empty! that's not allowed");
                }

                //检查被注解类是否实现或继承了@SimpleFactory.classType()所指定的类型
                if (!verifyAnnotationClassType(typeElement, simpleFactory.classType())) {
                    throw new IllegalArgumentException("The class " + typeElement.getQualifiedName().toString() + " annotated with @" + SimpleFactory.class.getSimpleName() + " must implement the interface " + simpleFactory.classType());
                }
                //验证包含Public构造方法
                Set<Modifier> modifiers = typeElement.getModifiers();
                if (!modifiers.contains(Modifier.PUBLIC)) {
                    throw new IllegalArgumentException("The class " + typeElement.getQualifiedName().toString() + " is not public.");
                } else if (modifiers.contains(Modifier.ABSTRACT)) {
                    //如果是抽象方法则抛出异常终止编译
                    throw new IllegalArgumentException("The class " + typeElement.getQualifiedName().toString() + " is abstract. You can't annotate abstract classes with " + SimpleFactory.class.getSimpleName() + "");
                }
                // 检查是否有public的无参构造方法
                boolean hasNoParamsConstructor = false;
                for (Element enclosed : typeElement.getEnclosedElements()) {
                    if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                        ExecutableElement constructorElement = (ExecutableElement) enclosed;
                        if (constructorElement.getParameters().size() == 0 && constructorElement.getModifiers().contains(Modifier.PUBLIC)) {
                            // 存在public的无参构造方法，检查结束
                            hasNoParamsConstructor = true;
                            break;
                        }
                    }
                }
                if (!hasNoParamsConstructor) {
                    // 未检测到public的无参构造方法，抛出异常，终止编译
                    throw new IllegalArgumentException("The class " + typeElement.getQualifiedName().toString() + " must provide an public empty default constructor");
                }
                //
            });
            mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> Verify Finished");


            String classNameStr = "MySimpleFactory";
            final StringBuilder classString = new StringBuilder();
            classString.append("package ").append(packageStr).append(";\n");
            classString.append("\npublic class ").append(classNameStr).append("{\n");

            elements.forEach((Consumer<Element>) element -> {
                TypeElement typeElement = (TypeElement) element;
                classString.append("\n\tpublic static ").append(typeElement.getQualifiedName()).append(" create").append(typeElement.getSimpleName()).append("(){\n");
                classString.append("\t\treturn new ").append(typeElement.getQualifiedName()).append("();\n");
                classString.append("\t}\n");
            });
            classString.append("}");
            mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> classString: \n" + classString);

            try {
                JavaFileObject javaFileObject = mFiler.createSourceFile(packageStr + "." + classNameStr);
                Writer writer = javaFileObject.openWriter();
                writer.flush();
                writer.append(classString.toString());
                writer.flush();
                writer.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->process--> end(" + r + ") >>>>>>>>>>>>>>>>>>>>>>>");
        r++;
        return true;
    }

    /**
     * 用来指定当前正在使用的Java版本，
     * 通常return SourceVersion.latestSupported()即可。
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private boolean verifyAnnotationClassType(TypeElement classElement, String annotationClassType) {

        if (classElement.getInterfaces().contains(mElementUtils.getTypeElement(annotationClassType).asType())) {
            return true;
        } else {
            String superClass = classElement.getSuperclass().toString();
            mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->verifyAnnotationClassType--> classElement.getSuperclass(): " + superClass);
            TypeElement superClassElement = mElementUtils.getTypeElement(superClass);
            if (superClassElement.getKind() == ElementKind.INTERFACE) {
                mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->verifyAnnotationClassType--> superClassElement.getKind() == ElementKind.INTERFACE: " + superClass);
            } else if (superClassElement.getKind() == ElementKind.CLASS) {
                mMessager.printMessage(Diagnostic.Kind.NOTE, "SimpleFactoryProcessor-->verifyAnnotationClassType--> superClassElement.getKind() == ElementKind.CLASS: " + superClass);
            }

            return verifyAnnotationClassType(superClassElement, annotationClassType);
        }
    }
}
