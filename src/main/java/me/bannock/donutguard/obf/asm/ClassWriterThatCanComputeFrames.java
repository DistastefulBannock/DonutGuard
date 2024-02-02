package me.bannock.donutguard.obf.asm;

import me.bannock.donutguard.obf.asm.impl.ClassEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ClassWriterThatCanComputeFrames extends ClassWriter {

    private final Logger logger = LogManager.getLogger();
    private final JarHandler parentHandler;

    public ClassWriterThatCanComputeFrames(int flags, JarHandler parentHanlder) {
        super(flags);
        this.parentHandler = parentHanlder;
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        // We need to maintain specific formatting, or we'll have issues
        type1 = type1.replace(".", "/");
        type2 = type2.replace(".", "/");

        // We need to override this because the class writer class is garbage
        try {
            return super.getCommonSuperClass(type1, type2);
        } catch (TypeNotPresentException e) {
            // ASM is horrible and checks the current runing JVM for the classes
            // rather than the classes loaded by the classreader.

            // We first comb through all of our loaded classes for the two types being compared
            ClassEntry match1, match2;
            match1 = findClassEntryWithType(type1);

            // If we can't find the class then it likely isn't in our
            // loaded classes. The user needs to add the correct library
            // to our loaded classes as we lack the data to return the proper
            // information. Running without compute frames/maxes usually resolves
            // this issue and can be used as a quick fix
            if (match1 == null){
                logStupidError(new ClassNotFoundException(type1));
                return "java/lang/Object";
            }

            match2 = findClassEntryWithType(type2);
            if (match2 == null){ // Read comment above
                logStupidError(new ClassNotFoundException(type2));
                return "java/lang/Object";
            }

            // We loop through every superclass until we reach object,
            // and then we do the same for the second class type but
            // this time return when we hit a match
            LinkedHashSet<String> supers1 = getAllSuperClassesForEntry(match1);
            LinkedHashSet<String> supers2 = getAllSuperClassesForEntry(match2);
            for (String superClass : supers2){
                if (supers1.contains(superClass))
                    return superClass;
            }

            return "java/lang/Object";
        }
    }

    /**
     * Recursive function that gets all super classes to one class.
     * Checks the super class as well as interfaces and feeds them back through
     * @param entry The starting entry
     * @return A LinkedHashSet containing al interfaces and superclasses of an object
     */
    private LinkedHashSet<String> getAllSuperClassesForEntry(ClassEntry entry){
        if (entry.getContent().interfaces == null)
            entry.getContent().interfaces = new ArrayList<>(); // Just so it isn't null.
                                                                // Not like it'll change output anyway

        LinkedHashSet<String> supers = new LinkedHashSet<>();
        if (entry.getContent().superName != null)
            supers.add(entry.getContent().superName);
        supers.addAll(entry.getContent().interfaces);
        if (entry.getContent().superName != null &&
                !entry.getContent().superName.equals("java/lang/Object")) { // Object will always be null
            ClassEntry superClass = findClassEntryWithType(entry.getContent().superName);
            if (superClass != null) // Not found
                supers.addAll(getAllSuperClassesForEntry(superClass));
        }
        for (String interfaceStr : entry.getContent().interfaces){ // Cannot implement object so no check for it
            ClassEntry interfaceEntry = findClassEntryWithType(interfaceStr);
            if (interfaceEntry != null)
                supers.addAll(getAllSuperClassesForEntry(interfaceEntry));
        }
        return supers;
    }

    private void logStupidError(ClassNotFoundException e){
        logger.warn("Unable to locate type. Please add the proper libraries or disable" +
                " compute frames", e);
    }

    /**
     * Finds a class entry by going down the linked list and comparing types.
     * Warning: This can be extremely resource intensive as the method is O(n)
     * and it isn't uncommon to have 20k+ classes loaded into the linked list
     * at one time. However, this is noted in the ASM docs and is needed
     * if we wish to compute frames the correct way
     * @param type The type to search for
     * @return The found class entry, or null if it isn't present in the linked list
     */
    private ClassEntry findClassEntryWithType(String type){
        if (type.toLowerCase().endsWith(".class"))
            type = type.substring(0, type.length() - 6);
        if (!parentHandler.getFirstEntry().containsPath(type))
            return null; // containsPath uses a HashSet and is O(1),
                         // so if we don't have the class then we won't waste resources searching

        type = type.replace(".", "/");
        FileEntry<?> current = parentHandler.getFirstEntry();
        while ((current = current.getNextNode()) != null){
            if (!(current instanceof ClassEntry))
                continue;
            ClassEntry entry = (ClassEntry) current;

            String path = entry.getPath();
            if (path.toLowerCase().endsWith(".class"))
                path = path.substring(0, path.length() - 6);
            path = path.replace(".", "/");

            if (path.equals(type))
                return entry;
        }
        return null; // Not found
    }

}
