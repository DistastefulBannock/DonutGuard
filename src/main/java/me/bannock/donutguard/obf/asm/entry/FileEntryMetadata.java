package me.bannock.donutguard.obf.asm.entry;

import java.util.HashSet;

public class FileEntryMetadata {

    /**
     * @param lastNode The last node on the linked list
     */
    protected FileEntryMetadata(FileEntry<?> lastNode){
        this.lastNode = lastNode;
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
