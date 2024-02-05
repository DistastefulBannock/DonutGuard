package me.bannock.donutguard.obf.asm;

import me.bannock.donutguard.obf.asm.impl.DummyEntry;
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
    private HashSet<FileEntry<?>> currentlyAddedNodes = new HashSet<>();

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
        if (getCurrentlyAddedNodes().contains(node)){
//            logger.warn("Node of same path is already present in linked list.");
            throw new IllegalArgumentException("Node of same path(\"" + node.getPath() +
                    "\") is already present in linked list.");
        }

        // We first have to get the last node in our linked list so we can add our
        // new node to the linked list
        FileEntry<?> currentNode = this;
        while (currentNode.getNextNode() != null)
            currentNode = currentNode.getNextNode();
        currentNode.setNextNode(node);
        node.setPreviousNode(currentNode);
        getCurrentlyAddedNodes().add(node);
        node.setCurrentlyAddedNodes(getCurrentlyAddedNodes());
    }

    /**
     * Removes this node from its self-managed linked list
     * and stitches the previous and next nodes together
     */
    public void removeNode(){
        FileEntry<?> previous = getPreviousNode();
        FileEntry<?> next = getNextNode();
        next.setPreviousNode(previous);
        next.setNextNode(next);
        setCurrentlyAddedNodes(new HashSet<>());
    }

    /**
     * Sets the path for this entry to something else,
     * manages reassigning it as well
     * @param path The new path to use
     */
    public void setPath(String path) {
        if (getCurrentlyAddedNodes().contains(this)){
            logger.error("Cannot set path to a path that is already in use");
            throw new IllegalArgumentException("Path is already in use");
        }
        getCurrentlyAddedNodes().remove(this);
        this.path = path;
        getCurrentlyAddedNodes().add(this);
    }

    /**
     * Checks if the self-managed linked list already contains an entry
     * @param entry The entry to check
     * @return True if the linked list contains the given entry, otherwise false
     */
    public boolean containsEntry(FileEntry<?> entry){
        return getCurrentlyAddedNodes().contains(entry);
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

    private HashSet<FileEntry<?>> getCurrentlyAddedNodes() {
        return currentlyAddedNodes;
    }

    private void setCurrentlyAddedNodes(HashSet<FileEntry<?>> currentlyAddedNodes) {
        this.currentlyAddedNodes = currentlyAddedNodes;
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
