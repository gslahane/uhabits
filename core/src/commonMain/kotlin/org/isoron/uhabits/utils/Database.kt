/*
 * Copyright (C) 2016-2019 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.utils

enum class StepResult {
    ROW,
    DONE
}

interface PreparedStatement {
    fun step(): StepResult
    fun finalize()
    fun getInt(index: Int): Int
    fun getText(index: Int): String
    fun getReal(index: Int): Double
    fun bindInt(index: Int, value: Int)
    fun bindText(index: Int, value: String)
    fun bindReal(index: Int, value: Double)
    fun reset()
}

interface Database {
    fun prepareStatement(sql: String): PreparedStatement
    fun close()
}

interface DatabaseOpener {
    fun open(file: UserFile): Database
}

fun Database.execute(sql: String) {
    val stmt = prepareStatement(sql)
    stmt.step()
    stmt.finalize()
}

fun Database.queryInt(sql: String): Int {
    val stmt = prepareStatement(sql)
    stmt.step()
    val result = stmt.getInt(0)
    stmt.finalize()
    return result
}

fun Database.nextId(tableName: String): Int {
    val stmt = prepareStatement("select seq from sqlite_sequence where name='$tableName'")
    if(stmt.step() == StepResult.ROW) {
        val result = stmt.getInt(0)
        stmt.finalize()
        return result + 1
    } else {
        return 0
    }
}

fun Database.begin() = execute("begin")

fun Database.commit() = execute("commit")

fun Database.getVersion() = queryInt("pragma user_version")

fun Database.setVersion(v: Int) = execute("pragma user_version = $v")

fun Database.migrateTo(newVersion: Int, fileOpener: FileOpener, log: Log) {
    val currentVersion = getVersion()
    log.debug("Database", "Current database version: $currentVersion")

    if (currentVersion == newVersion) return
    log.debug("Database", "Upgrading to version: $newVersion")

    if (currentVersion > newVersion)
        throw RuntimeException("database produced by future version of the application")
    
    begin()
    for (v in (currentVersion + 1)..newVersion) {
        log.debug("Database", "Running migration $v")
        val filename = sprintf("migrations/%03d.sql", v)
        val migrationFile = fileOpener.openResourceFile(filename)
        for (line in migrationFile.readLines()) {
            if (line.isEmpty()) continue
            execute(line)
        }
        setVersion(v)
    }
    commit()
}
