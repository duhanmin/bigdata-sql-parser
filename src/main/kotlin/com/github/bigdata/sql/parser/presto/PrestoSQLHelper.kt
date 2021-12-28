package com.github.bigdata.sql.parser.presto

import io.trino.sql.parser.ParsingOptions
import io.trino.sql.parser.SqlParser
import io.trino.sql.tree.*
import com.github.bigdata.sql.parser.StatementData
import com.github.bigdata.sql.parser.StatementType
import com.github.bigdata.sql.parser.TableData
import com.github.bigdata.sql.parser.TableSource
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang.StringUtils


/**
 *
 * Author: duhanmin
 * Description:
 * Date: 2021/8/17 15:26
 */
object PrestoSQLHelper {
    private val SQL_PARSER = SqlParser()

    @JvmStatic fun getStatementData(sql: String): StatementData{
        val statementData = TableData()
        val parsingOptions = ParsingOptions(ParsingOptions.DecimalLiteralTreatment.AS_DOUBLE)
        val statement = SQL_PARSER.createStatement(sql, parsingOptions)

        when (statement) {
            is Insert -> {
                statementData.outpuTables.add(tableSource(statement.target.toString()))
            }
        }
        maxDepthLeaf(statement.children,statementData)

        val hashSet = HashSet(statementData.inputTables)
        statementData.inputTables.clear()
        statementData.inputTables.addAll(hashSet)
        return StatementData(StatementType.UNKOWN, statementData)
    }

    private fun tableSource(str: String): TableSource {
        val split = StringUtils.split(str, ".")
        var tableSource: TableSource = when (split.size) {
            2 -> {
                TableSource(split[0], split[1])
            }
            3 -> {
                TableSource(split[1], split[2])
            }
            1 -> {
                TableSource("", split[0])
            }
            else -> {
                TableSource("", "")
            }
        }
        return tableSource
    }

    private fun maxDepthLeaf(treeList: List<Node>,statement:TableData) {
        if (CollectionUtils.isNotEmpty(treeList)) {
            for (node in treeList) {

                val children = node.children

                if (node is QuerySpecification){
                    val from = node.from.get()
                    if(from is Table){
                        statement.inputTables.add(tableSource(from.name.toString()))
                        //println("输入表:"+from.name + "----------输入字段:"+node.select.selectItems)
                    }
                }

                if(node is Table){
                    statement.inputTables.add(tableSource(node.name.toString()))
                }

                if (CollectionUtils.isNotEmpty(children)) {
                    maxDepthLeaf(children,statement)
                }
            }
        }
    }
}
