package cge.zeppelin;
/**
 * The application is notified of collisions via this interface. The implementor
 * is called on collision of two entities.
 */
interface CollisionListener {
    /**
     * Called with colliding entities.
     */
    void response(Entity e0, Entity e1);
}
