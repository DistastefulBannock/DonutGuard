package me.bannock.donutguard.obf.asm.entry;

import me.bannock.donutguard.obf.asm.entry.impl.DummyEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Objects;

/**
 * This class is an abstraction for entries inside of zip/jar files.
 * It has a self-managed linked list that keeps track of all entries
 * in an archive. You are only able to add nodes to the end or remove them,
 * you are not able to insert them in the middle.
 * @param <T> The type of content that this entry contains
 */
public abstract class FileEntry<T> {

    private final Logger logger = LogManager.getLogger();

    private String path;
    private final boolean shouldMutate, libraryEntry;
    private T content;
    private FileEntry<?> previousNode = null, nextNode = null;
    private FileEntryMetadata linkedListMetadata = new FileEntryMetadata(this);

    /**
     * @param path The path of the entry inside the jar
     * @param shouldMutate Whether the program should mutate this entry
     * @param libraryEntry Whether the class is part of a library
     * @param content The content of the entry
     */
    public FileEntry(String path, boolean shouldMutate, boolean libraryEntry, T content){
        this.path = path;
        this.shouldMutate = shouldMutate;
        this.libraryEntry = libraryEntry;
        this.content = content;
    }

    /**
     * Adds a node to the self-managed linked list
     * @param node The node to add
     */
    public void addNodeToEnd(FileEntry<?> node){
        if (node.getPreviousNode() != null || node.getNextNode() != null){
            logger.warn("Node being is already inside of a linked list");
            throw new IllegalArgumentException("Node cannot already be in a linked list");
        }
        if (linkedListMetadata.getCurrentlyAddedNodes().contains(node)){
//            logger.warn("Node of same path is already present in linked list.");
            throw new IllegalArgumentException("Node of same path(\"" + node.getPath() +
                    "\") is already present in linked list.");
        }

        // We first have to get the last node in our linked list so we can add our
        // new node to the linked list
        FileEntry<?> lastNode = linkedListMetadata.getLastNode();
        lastNode.setNextNode(node);
        node.setPreviousNode(lastNode);
        linkedListMetadata.setLastNode(node);
        linkedListMetadata.getCurrentlyAddedNodes().add(node);
        node.setLinkedListMetadata(linkedListMetadata);
    }

    /**
     * Removes this node from its self-managed linked list
     * and stitches the previous and next nodes together
     */
    public void removeNode(){
        FileEntry<?> previous = getPreviousNode();
        FileEntry<?> next = getNextNode();
        if (this == linkedListMetadata.getLastNode()){
            linkedListMetadata.setLastNode(previousNode);
        }
        setLinkedListMetadata(new FileEntryMetadata(this));
        if (next != null)
            next.setPreviousNode(previous);
        if (previous != null)
            previous.setNextNode(next);
        linkedListMetadata.setCurrentlyAddedNodes(new HashSet<>());
    }

    /**
     * Sets the path for this entry to something else,
     * manages reassigning it as well
     * @param path The new path to use
     */
    public void setPath(String path) {
        if (linkedListMetadata.getCurrentlyAddedNodes().contains(this)){
            logger.error("Cannot set path to a path that is already in use");
            throw new IllegalArgumentException("Path is already in use");
        }
        linkedListMetadata.getCurrentlyAddedNodes().remove(this);
        this.path = path;
        linkedListMetadata.getCurrentlyAddedNodes().add(this);
    }

    /**
     * Checks if the self-managed linked list already contains an entry
     * @param entry The entry to check
     * @return True if the linked list contains the given entry, otherwise false
     */
    public boolean containsEntry(FileEntry<?> entry){
        return linkedListMetadata.getCurrentlyAddedNodes().contains(entry);
    }

    /**
     * Checks if the self-managed linked list already contains a path
     * @param path The path to check
     * @return True if the linked list contains the given path, otherwise false
     */
    public boolean containsPath(String path){
        return containsEntry(new DummyEntry(path));
    }

    public String getPath() {
        return path;
    }

    public boolean isShouldMutate() {
        return shouldMutate;
    }

    public boolean isLibraryEntry() {
        return libraryEntry;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public FileEntry<?> getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(FileEntry<?> previousNode) {
        this.previousNode = previousNode;
    }

    public FileEntry<?> getNextNode() {
        return nextNode;
    }

    public void setNextNode(FileEntry<?> nextNode) {
        this.nextNode = nextNode;
    }

    public FileEntryMetadata getLinkedListMetadata() {
        return linkedListMetadata;
    }

    private void setLinkedListMetadata(FileEntryMetadata linkedListMetadata) {
        this.linkedListMetadata = linkedListMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntry<?> fileEntry = (FileEntry<?>) o;
        return Objects.equals(getPath(), fileEntry.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath());
    }

}
