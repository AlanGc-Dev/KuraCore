package com.kuraky.database

/**
 * ORM Dinámico y minimalista para KuraCore.
 * Permite hacer operaciones CRUD sin necesidad de crear Data Classes estrictas.
 */
class KuraOrm(private val pool: KuraSqlPool) {

    /**
     * Crea una tabla especificando el nombre y las columnas dinámicamente.
     * Ejemplo: orm.createTable("jugadores", "uuid" to "VARCHAR(36) PRIMARY KEY", "nivel" to "INT")
     */
    fun createTable(tableName: String, vararg columns: Pair<String, String>) {
        val colsDef = columns.joinToString(", ") { "${it.first} ${it.second}" }
        val query = "CREATE TABLE IF NOT EXISTS $tableName ($colsDef)"

        pool.getConnection().use { conn ->
            conn.prepareStatement(query).use { it.execute() }
        }
    }

    /**
     * Inserta un nuevo registro en la tabla.
     * Ejemplo: orm.insert("jugadores", "uuid" to "123", "nivel" to 1, "xp" to 0.0)
     */
    fun insert(tableName: String, vararg data: Pair<String, Any>) {
        val columns = data.joinToString(", ") { it.first }
        val valuesPlaceholder = data.joinToString(", ") { "?" }
        val query = "INSERT INTO $tableName ($columns) VALUES ($valuesPlaceholder)"

        pool.getConnection().use { conn ->
            conn.prepareStatement(query).use { stmt ->
                data.forEachIndexed { index, pair ->
                    stmt.setObject(index + 1, pair.second)
                }
                stmt.executeUpdate()
            }
        }
    }

    /**
     * Obtiene UN registro de la base de datos y lo devuelve como un Diccionario (Map).
     * Ejemplo: val datos = orm.get("jugadores", "uuid" to "123")
     */
    fun get(tableName: String, vararg where: Pair<String, Any>): Map<String, Any>? {
        val whereClause = where.joinToString(" AND ") { "${it.first} = ?" }
        val query = "SELECT * FROM $tableName" + if (where.isNotEmpty()) " WHERE $whereClause" else ""

        pool.getConnection().use { conn ->
            conn.prepareStatement(query).use { stmt ->
                where.forEachIndexed { index, pair ->
                    stmt.setObject(index + 1, pair.second)
                }
                val rs = stmt.executeQuery()

                if (rs.next()) {
                    val metaData = rs.metaData
                    val result = mutableMapOf<String, Any>()
                    for (i in 1..metaData.columnCount) {
                        result[metaData.getColumnName(i)] = rs.getObject(i)
                    }
                    return result
                }
                return null
            }
        }
    }

    /**
     * Actualiza registros existentes.
     * Ejemplo: orm.update("jugadores", where = "uuid" to "123", "nivel" to 2, "xp" to 50.5)
     */
    fun update(tableName: String, where: Pair<String, Any>, vararg data: Pair<String, Any>) {
        val setClause = data.joinToString(", ") { "${it.first} = ?" }
        val query = "UPDATE $tableName SET $setClause WHERE ${where.first} = ?"

        pool.getConnection().use { conn ->
            conn.prepareStatement(query).use { stmt ->
                var index = 1
                // Asignamos los datos a actualizar
                data.forEach { pair ->
                    stmt.setObject(index++, pair.second)
                }
                // Asignamos la condición del WHERE
                stmt.setObject(index, where.second)
                stmt.executeUpdate()
            }
        }
    }

    /**
     * Elimina un registro entero.
     * Ejemplo: orm.delete("jugadores", "uuid" to "123")
     */
    fun delete(tableName: String, vararg where: Pair<String, Any>) {
        val whereClause = where.joinToString(" AND ") { "${it.first} = ?" }
        val query = "DELETE FROM $tableName WHERE $whereClause"

        pool.getConnection().use { conn ->
            conn.prepareStatement(query).use { stmt ->
                where.forEachIndexed { index, pair ->
                    stmt.setObject(index + 1, pair.second)
                }
                stmt.executeUpdate()
            }
        }
    }
}