package it.polimi.ingsw.controller.db;

/**
 * Represents objects which can provide a name which uniquely identifies them.
 * Identifiables are meant to be saved on disk. The name is used as an ID and allows to retrieve
 * the corresponding Identifiable among the ones saved on disk.
 *
 * @author Cristiano Migali
 */
public interface Identifiable {
    /**
     * @return the name which uniquely identifies this Identifiable.
     */
    String getName();
}
