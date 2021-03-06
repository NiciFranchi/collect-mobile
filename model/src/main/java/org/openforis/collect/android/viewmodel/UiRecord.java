package org.openforis.collect.android.viewmodel;

import java.util.*;

/**
 * @author Daniel Wiell
 */
public class UiRecord extends UiEntity {
    private Map<Integer, UiNode> nodeById = new HashMap<Integer, UiNode>();

    public UiRecord(int id, Definition definition, UiRecordCollection recordCollection, Placeholder placeholder) {
        super(id, true, definition);
        setParent(recordCollection);
        placeholder.keyAttributes = getKeyAttributes();
    }

    public UiRecord(int id, Definition definition, UiRecordCollection recordCollection) {
        super(id, true, definition);
        setParent(recordCollection);
    }

    public List<UiNode> updateStatusOfNodeAndParents(Status status) {
        setStatus(status);
        getParent().getChildById(getId()).setStatus(status); // Update record placeholder status
        ArrayList<UiNode> updatedNodes = new ArrayList<UiNode>();
        updatedNodes.add(this);
        return updatedNodes;
    }

    public void register(UiNode node) {
        super.register(node);
        nodeById.put(node.getId(), node);
    }

    public void unregister(UiNode node) {
        super.unregister(node);
        nodeById.remove(node.getId());
    }

    public UiNode lookupNode(int nodeId) {
        if (nodeId == getId())
            return this;
        return nodeById.get(nodeId); // TODO: Throw exception if not found?
    }

    public Placeholder createPlaceholder() {
        return new Placeholder(this);
    }


    public UiRecordCollection getParent() {
        return (UiRecordCollection) super.getParent();
    }

    public void keyAttributeUpdated() {
        Placeholder placeholder = (Placeholder) getParent().getChildById(getId());
        placeholder.keyAttributes = getKeyAttributes();
    }

    public void modifiedOnUpdated() {
        Placeholder placeholder = (Placeholder) getParent().getChildById(getId());
        placeholder.setModifiedOn(getModifiedOn());
    }

    public static class Placeholder extends UiNode {
        private final String recordCollectionName;
        private List<UiAttribute> keyAttributes;

        private Placeholder(UiRecord record) {
            this(record.getId(), record.getStatus(), record.getParent().getDefinition().name,
                    record.getDefinition(), record.getKeyAttributes(), record.getModifiedOn());
        }

        public Placeholder(int id, Status status, String recordCollectionName, Definition definition,
                           List<UiAttribute> keyAttributes, Date modifiedOn) {
            super(id, true, definition);
            this.setStatus(status);
            this.setModifiedOn(modifiedOn);
            this.recordCollectionName = recordCollectionName;
            this.keyAttributes = new ArrayList<UiAttribute>(keyAttributes);
        }

        public String getRecordCollectionName() {
            return recordCollectionName;
        }

        public List<UiAttribute> getKeyAttributes() {
            return Collections.unmodifiableList(keyAttributes);
        }
    }
}