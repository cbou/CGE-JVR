package cge.zepelin;
/**
 * A pair of colliding entities. Comparison and hashing are invariant to entity
 * ordering.
 */
public class CollisionPair {
    final Entity e0, e1;

    /**
     * Construct a new pair. Entities must not be null.
     */
    CollisionPair(Entity a, Entity b) {
        e0 = a;
        e1 = b;
    }

    
    /**
     * Check if an entity is included in the pair.
     */
    boolean contains(Entity e) {
        return e0.equals(e) || e1.equals(e);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object a) {
        if (!(a instanceof CollisionPair))
            return false;
        CollisionPair cp = (CollisionPair) a;
        return e0.equals(cp.e0) && e1.equals(cp.e1) || e0.equals(cp.e1) && e1.equals(cp.e0);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return e0.hashCode() * e1.hashCode();
    }
}
