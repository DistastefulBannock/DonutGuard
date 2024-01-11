package me.bannock.donutguard.obf.asm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Objects;

public abstract class FileEntry<T> {

    private final Logger logger = LogManager.getLogger();

    private String path;
    private final boolean resource, shouldMutate;
    private T content;
    private FileEntry<?> previousNode = null, nextNode = null;
    private HashSet<FileEntry<?>> currentlyAddedNodes = new HashSet<>();

    /**
     * @param path The path of the entry inside the jar
     * @param isResource Whether the entry is a resource
     * @param shouldMutate Whether the program should mutate this entry
     * @param content The content of the entry
     */
    public FileEntry(String path, boolean isResource, boolean shouldMutate, T content){
        this.path = path;
        this.resource = isResource;
        this.shouldMutate = shouldMutate;
        this.content = content;
    }

    /**
     * Adds a node to the self-managed linked list
     * @param node The node to add
     */
    public void addNodeToEnd(FileEntry<?> node){
        if (node.getPreviousNode() != null || node.getNextNode() != null){
            logger.error("Node being is already inside of a linked list");
            throw new IllegalArgumentException("Node cannot already be in a linked list");
        }
        if (getCurrentlyAddedNodes().contains(node)){
            logger.error("Node of same path is already present in linked list.");
            throw new IllegalArgumentException("Node of same path is already present in linked list.");
        }

        // We first have to get the last node in our linked list so we can add our
        // new node to the linked list
        FileEntry<?> currentNode = this;
        while (currentNode.getNextNode() != null)
            currentNode = currentNode.getNextNode();
        currentNode.setNextNode(node);
        node.setPreviousNode(currentNode);
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

    public String getPath() {
        return path;
    }

    public boolean isResource() {
        return resource;
    }

    public boolean isShouldMutate() {
        return shouldMutate;
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
