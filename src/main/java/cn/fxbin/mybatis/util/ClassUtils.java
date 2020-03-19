package cn.fxbin.mybatis.util;

import sun.awt.image.ImageWatched;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * ClassUtils
 *
 * @author fxbin
 * @version v1.0
 * @since 2020/3/19 14:14
 */
public class ClassUtils {

    /**
     * getClassLoader
     *
     * @author fxbin
     * @since 2020/3/19 14:15
     * @return java.lang.ClassLoader
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }


    /**
     * loadClass
     *
     * @author fxbin
     * @since 2020/3/19 14:15
     * @param className class name
     * @param isInitialized 是否执行类的静态代码块
     * @return java.lang.Class<T>
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return clazz;
    }


    /**
     * getAllClassByPackageName 通过包名获取包内所有类
     *
     * @author fxbin
     * @since 2020/3/19 15:47
     * @param pkg java.lang.Package
     * @return java.util.Set<java.lang.Class<?>>
     */
    public static Set<Class<?>> getAllClassByPackageName(Package pkg) {
        String packageName = pkg.getName();
        return getClasses(packageName);
    }


    /**
     * getAllClassByInterface
     *
     * @author fxbin
     * @since 2020/3/19 15:50
     * @param c java.lang.Class<?>
     * @return java.util.Set<java.lang.Class<?>>
     */
    public static Set<Class<?>> getAllClassByInterface(Class<?> c) {
        Set<Class<?>> returnClassList = null;

        if (c.isInterface()) {
            // 获取当前的包名
            String packageName = c.getPackage().getName();
            // 获取当前包下以及子包下所以的类
            Set<Class<?>> allClass = getClasses(packageName);
            if (allClass != null) {
                returnClassList = new LinkedHashSet<Class<?>>();
                for (Class<?> cls : allClass) {
                    // 判断是否是同一个接口
                    if (c.isAssignableFrom(cls)) {
                        // 本身不加入进去
                        if (!c.equals(cls)) {
                            returnClassList.add(cls);
                        }
                    }
                }
            }
        }
        return returnClassList;
    }


    /**
     * getPackageAllClassName 取得某一类所在包的所有类名 不含迭代
     *
     * @author fxbin
     * @since 2020/3/19 15:51
     * @param classLocation class location
     * @param packageName package name
     * @return java.lang.String[]
     */
    public static String[] getPackageAllClassName(String classLocation, String packageName) {
        // 将packageName分解
        String[] packagePathSplit = packageName.split("[.]");
        String realClassLocation = classLocation;
        int packageLength = packagePathSplit.length;
        for (int i = 0; i < packageLength; i++) {
            realClassLocation = realClassLocation + File.separator + packagePathSplit[i];
        }
        File packeageDir = new File(realClassLocation);
        if (packeageDir.isDirectory()) {
            String[] allClassName = packeageDir.list();
            return allClassName;
        }
        return null;
    }


    /**
     * getClasses 从包package中获取所有的Class
     *
     * @author fxbin
     * @since 2020/3/19 15:52
     * @param packageName package name
     * @return java.util.Set<java.lang.Class<?>>
     */
    public static Set<Class<?>> getClasses(String packageName) {
        // class类的集合
        Set<Class<?>> classes = new LinkedHashSet<>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个美剧的集合 并进行循环来处理这个目录下的内容
        Enumeration<URL> dirs;

        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 获取协议名称
                String protocol = url.getProtocol();
                // 如果是以文件形式保存在服务器
                if("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件， 定义一个 JarFile
                    JarFile jar;

                    try {
                        // 获取jar包
                        jar = ((JarURLConnection)url.openConnection()).getJarFile();
                        // 从此jar包得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 迭代循环
                        while (entries.hasMoreElements()) {
                            // 获取jar里的实体，可以是目录和一些jar包里的其它文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/" 结尾，是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }

                                // 如果是一个.class文件 而且不是目录
                                if (name.endsWith(".class") && !entry.isDirectory()) {
                                    // 去掉后面的".class" 获取真正的类名
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                                    try {
                                        // 添加到classes
                                        classes.add(Class.forName(packageName + '.' + className));
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;

    }


    /**
     * findAndAddClassesInPackageByFile 以文件的形式来获取包下的所有Class
     *
     * @author fxbin
     * @since 2020/3/19 15:14
     * @param packageName package name
     * @param packagePath package path
     * @param recursive 是否递归
     * @param classes classes
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录， 建立一个file
        File dir = new File(packagePath);
        // 如果不存在或者 不是目录就直接返回
        if(!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件
        File [] dirFiles = dir.listFiles((file) -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        // 循环所有文件
        for (File file : Objects.requireNonNull(dirFiles)) {
            // 如果是目录，继续扫描
            if(file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是 java类文件， 去掉后面的.class, 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
