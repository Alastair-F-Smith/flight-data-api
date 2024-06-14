package com.example.afs.flightdataapi.services.converters;

import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserType;
import org.postgresql.geometric.PGpoint;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PGPointType implements UserType<PGpoint> {


    /**
     * The JDBC/SQL type code for the database column mapped by this
     * custom type.
     * <p>
     * The type code is usually one of the standard type codes
     * declared by {@link SqlTypes}, but it could
     * be a database-specific code.
     *
     * @see SqlTypes
     */
    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    /**
     * The class returned by nullSafeGet().
     * @return Class
     */
    @Override
    public Class returnedClass() {
        return PGpoint.class;
    }

    /**
     * Compare two instances of the Java class mapped by this custom
     * type for persistence "equality", that is, equality of their
     * persistent state.
     *
     * @param x
     * @param y
     */
    @Override
    public boolean equals(PGpoint x, PGpoint y) {
        return ObjectUtils.nullSafeEquals(x, y);
    }

    /**
     * Get a hash code for the given instance of the Java class mapped
     * by this custom type, consistent with the definition of
     * {@linkplain #equals(PGpoint, PGpoint) persistence "equality"} for
     * this custom type.
     *
     * @param x
     */
    @Override
    public int hashCode(PGpoint x) {
        return ObjectUtils.nullSafeHashCode(x);
    }

    /**
     * Read an instance of the Java class mapped by this custom type
     * from the given JDBC {@link ResultSet}. Implementors must handle
     * null column values.
     *
     * @param rs
     * @param position
     * @param session
     * @param owner    in Hibernate 6, this is always null
     */
    @Override
    public PGpoint nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        if (rs.wasNull() || rs.getObject(position) == null) {
            return null;
        } else {
            return new PGpoint(rs.getObject(position).toString());
        }
    }

    /**
     * Write an instance of the Java class mapped by this custom type
     * to the given JDBC {@link PreparedStatement}. Implementors must
     * handle null values of the Java class. A multi-column type should
     * be written to parameters starting from {@code index}.
     *
     * @param st
     * @param value
     * @param index
     * @param session
     */
    @Override
    public void nullSafeSet(PreparedStatement st, PGpoint value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.toString(), Types.OTHER);
        }
    }

    /**
     * Return a clone of the given instance of the Java class mapped
     * by this custom type.
     * <ul>
     * <li>It's not necessary to clone immutable objects. If the Java
     *     class mapped by this custom type is an immutable class,
     *     this method may safely just return its argument.
     * <li>For mutable objects, it's necessary to deep copy persistent
     *     state, stopping at associations to other entities, and at
     *     persistent collections.
     * <li>If the argument is a reference to an entity, just return
     *     the argument.
     * <li>Finally, if the argument is null, just return null.
     * </ul>
     *
     * @param value the object to be cloned, which may be null
     * @return a clone
     */
    @Override
    public PGpoint deepCopy(PGpoint value) {
        return new PGpoint(value.x, value.y);
    }


    @Override
    public boolean isMutable() {
        return false;
    }

    /**
     * Transform the given value into a destructured representation,
     * suitable for storage in the {@linkplain Cache
     * second-level cache}. This method is called only during the
     * process of writing the properties of an entity to the
     * second-level cache.
     * <p>
     * If the value is mutable then, at the very least, this method
     * should perform a deep copy. That may not be enough for some
     * types, however. For example, associations must be cached as
     * identifier values.
     * <p>
     * This is an optional operation, but, if left unimplemented,
     * this type will not be cacheable in the second-level cache.
     *
     * @param value the object to be cached
     * @return a cacheable representation of the object
     * @see Cache
     */
    @Override
    public Serializable disassemble(PGpoint value) {
        return value;
    }

    /**
     * Reconstruct a value from its destructured representation,
     * during the process of reading the properties of an entity
     * from the {@linkplain Cache second-level cache}.
     * <p>
     * If the value is mutable then, at the very least, this method
     * should perform a deep copy. That may not be enough for some
     * types, however. For example, associations must be cached as
     * identifier values.
     * <p>
     * This is an optional operation, but, if left unimplemented,
     * this type will not be cacheable in the second-level cache.
     *
     * @param cached the object to be cached
     * @param owner  the owner of the cached object
     * @return a reconstructed object from the cacheable representation
     * @see Cache
     */
    @Override
    public PGpoint assemble(Serializable cached, Object owner) {
        return (PGpoint) cached;
    }


}
