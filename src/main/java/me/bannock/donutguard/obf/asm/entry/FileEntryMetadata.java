package me.bannock.donutguard.obf.asm.entry;

import java.util.HashSet;

/**
 * Metadata object for FileEntry class. Every node in the FileEntry's self-managed linked list
 * is given the same metadata object. This allows for them to easily grab the end of the list,
 * or the content of the list in the form of a hashset.
 */
public class FileEntryMetadata {

    /**
     * @param lastNode The last node on the linked list
     */
    protected FileEntryMetadata(FileEntry<?> lastNode){
        this.lastNode = lastNode;
        currentlyAddedNodes.add(lastNode);
    }

    private HashSet<FileEntry<?>> currentlyAddedNodes = new HashSet<>();
    private FileEntry<?> lastNode = null;

    public HashSet<FileEntry<?>> getCurrentlyAddedNodes() {
        return currentlyAddedNodes;
    }

    protected void setCurrentlyAddedNodes(HashSet<FileEntry<?>> currentlyAddedNodes) {
        this.currentlyAddedNodes = currentlyAddedNodes;
    }

    public FileEntry<?> getLastNode() {
        return lastNode;
    }

    protected void setLastNode(FileEntry<?> lastNode) {
        this.lastNode = lastNode;
    }

}
