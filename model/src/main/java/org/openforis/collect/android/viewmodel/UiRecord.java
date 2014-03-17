package org.openforis.collect.android.viewmodel;

import java.util.*;

/**
 * @author Daniel Wiell
 */
public class UiRecord extends UiEntity {
    private Map<Integer, UiNode> nodeById = new HashMap<Integer, UiNode>();

    public UiRecord(int id, Definition definition, UiRecordCollection recordCollection, Placeholder placeholder) {
        super(id, definition);
        setParent(recordCollection);
        placeholder.keyAttributes = getKeyAttributes();
    }

    public UiRecord(int id, Definition definition, UiRecordCollection recordCollection) {
        super(id, definition);
        setParent(recordCollection);
    }

    public void register(UiNode node) {
        super.register(node);
        nodeById.put(node.getId(), node);
    }

    public UiNode lookupNode(int nodeId) {
        return nodeById.get(nodeId); // TODO: Throw exception if not found?
    }

    public Placeholder createPlaceholder() {
        return new Placeholder(this);
    }


    public UiRecordCollection getParent() {
        return (UiRecordCollection) super.getParent();
    }

    public static class Placeholder extends UiNode {
        private final String recordCollectionName;
        private List<UiAttribute> keyAttributes;

        private Placeholder(UiRecord record) {
            this(record.getId(), record.getParent().getDefinition().name, record.getDefinition(), record.getKeyAttributes());
        }

        public Placeholder(int id, String recordCollectionName, Definition definition, List<UiAttribute> keyAttributes) {
            super(id, definition);
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