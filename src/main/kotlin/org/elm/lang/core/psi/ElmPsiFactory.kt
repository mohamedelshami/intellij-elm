package org.elm.lang.core.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import org.elm.lang.core.ElmFileType
import org.elm.lang.core.psi.ElmTypes.*
import org.elm.lang.core.psi.elements.*


class ElmPsiFactory(private val project: Project)
{
    companion object {
        /**
         * WARNING: this should only be called from the [ParserDefinition] hook
         * which takes [ASTNode]s from the [PsiBuilder] and emits [PsiElement].
         *
         * IMPORTANT: Must be kept in-sync with the BNF
         */
        fun createElement(node: ASTNode): PsiElement {
            when (node.elementType) {
                ANONYMOUS_FUNCTION -> return ElmAnonymousFunction(node)
                AS_CLAUSE -> return ElmAsClause(node)
                CASE_OF -> return ElmCaseOf(node)
                CASE_OF_BRANCH -> return ElmCaseOfBranch(node)
                EXPOSED_TYPE -> return ElmExposedType(node)
                EXPOSED_VALUE -> return ElmExposedValue(node)
                EXPOSED_UNION_CONSTRUCTORS -> return ElmExposedUnionConstructors(node)
                EXPOSED_UNION_CONSTRUCTOR -> return ElmExposedUnionConstructor(node)
                EXPOSING_LIST -> return ElmExposingList(node)
                EXPRESSION -> return ElmExpression(node)
                FIELD -> return ElmField(node)
                FIELD_TYPE -> return ElmFieldType(node)
                FUNCTION_DECLARATION_LEFT -> return ElmFunctionDeclarationLeft(node)
                GLSL_CODE -> return ElmGlslCode(node)
                IF_ELSE -> return ElmIfElse(node)
                IMPORT_CLAUSE -> return ElmImportClause(node)
                INNER_TYPE_ANNOTATION -> return ElmInnerTypeAnnotation(node)
                INNER_VALUE_DECLARATION -> return ElmInnerValueDeclaration(node)
                LET_IN -> return ElmLetIn(node)
                LIST -> return ElmList(node)
                LIST_OF_OPERANDS -> return ElmListOfOperands(node)
                LOWER_PATTERN -> return ElmLowerPattern(node)
                LOWER_TYPE_NAME -> return ElmLowerTypeName(node)
                MODULE_DECLARATION -> return ElmModuleDeclaration(node)
                NON_EMPTY_TUPLE -> return ElmNonEmptyTuple(node)
                OPERATOR_AS_FUNCTION -> return ElmOperatorAsFunction(node)
                OPERATOR_CONFIG -> return ElmOperatorConfig(node)
                OPERATOR_DECLARATION_LEFT -> return ElmOperatorDeclarationLeft(node)
                PARAMETRIC_TYPE_REF -> return ElmParametricTypeRef(node)
                PARENTHESED_EXPRESSION -> return ElmParenthesedExpression(node)
                PATTERN -> return ElmPattern(node)
                PATTERN_AS -> return ElmPatternAs(node)
                PORT_ANNOTATION -> return ElmPortAnnotation(node)
                RECORD -> return ElmRecord(node)
                RECORD_PATTERN -> return ElmRecordPattern(node)
                RECORD_TYPE -> return ElmRecordType(node)
                TUPLE_CONSTRUCTOR -> return ElmTupleConstructor(node)
                TUPLE_PATTERN -> return ElmTuplePattern(node)
                TUPLE_TYPE -> return ElmTupleType(node)
                TYPE_ALIAS_DECLARATION -> return ElmTypeAliasDeclaration(node)
                TYPE_ANNOTATION -> return ElmTypeAnnotation(node)
                TYPE_DECLARATION -> return ElmTypeDeclaration(node)
                TYPE_REF -> return ElmTypeRef(node)
                TYPE_VARIABLE_REF -> return ElmTypeVariableRef(node)
                UNION_MEMBER -> return ElmUnionMember(node)
                UNION_PATTERN -> return ElmUnionPattern(node)
                UNIT -> return ElmUnit(node)
                UPPER_PATH_TYPE_REF -> return ElmUpperPathTypeRef(node)
                VALUE_DECLARATION -> return ElmValueDeclaration(node)
                VALUE_EXPR -> return ElmValueExpr(node)
                else -> throw AssertionError("Unknown element type: " + node.elementType)
            }
        }
    }

    fun createLowerCaseIdentifier(text: String): PsiElement =
            createFromText("$text = 42", LOWER_CASE_IDENTIFIER)
                    ?: error("Failed to create lower-case identifier: `$text`")

    fun createUpperCaseIdentifier(text: String): PsiElement =
            createFromText<ElmTypeAliasDeclaration>("type alias $text = Int")
                    ?.upperCaseIdentifier
                    ?: error("Failed to create upper-case identifier: `$text`")

    fun createUpperCaseQID(text: String): ElmUpperCaseQID =
            createFromText<ElmModuleDeclaration>("module $text exposing (..)")
                    ?.upperCaseQID
                    ?: error("Failed to create upper-case QID: `$text`")

    fun createValueQID(text: String): ElmValueQID =
            createFromText<ElmValueDeclaration>("f = $text")
                    ?.expression
                    ?.childOfType<ElmValueQID>()
                    ?: error("Failed to create value QID: `$text`")

    private inline fun <reified T : PsiElement> createFromText(code: String): T? =
            PsiFileFactory.getInstance(project)
                    .createFileFromText("DUMMY.elm", ElmFileType, code)
                    .childOfType<T>()

    private fun createFromText(code: String, elementType: IElementType): PsiElement? =
            PsiFileFactory.getInstance(project)
                    .createFileFromText("DUMMY.elm", ElmFileType, code)
                    .descendantOfType(elementType)
}