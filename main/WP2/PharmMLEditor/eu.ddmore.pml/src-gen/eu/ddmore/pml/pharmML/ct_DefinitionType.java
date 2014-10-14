/**
 */
package eu.ddmore.pml.pharmML;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ct Definition Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link eu.ddmore.pml.pharmML.ct_DefinitionType#getXmlns <em>Xmlns</em>}</li>
 *   <li>{@link eu.ddmore.pml.pharmML.ct_DefinitionType#getDefinition <em>Definition</em>}</li>
 * </ul>
 * </p>
 *
 * @see eu.ddmore.pml.pharmML.PharmMLPackage#getct_DefinitionType()
 * @model
 * @generated
 */
public interface ct_DefinitionType extends EObject
{
  /**
   * Returns the value of the '<em><b>Xmlns</b></em>' containment reference list.
   * The list contents are of type {@link eu.ddmore.pml.pharmML.XS_xmlns}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Xmlns</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Xmlns</em>' containment reference list.
   * @see eu.ddmore.pml.pharmML.PharmMLPackage#getct_DefinitionType_Xmlns()
   * @model containment="true"
   * @generated
   */
  EList<XS_xmlns> getXmlns();

  /**
   * Returns the value of the '<em><b>Definition</b></em>' containment reference list.
   * The list contents are of type {@link eu.ddmore.pml.pharmML.ct_ColumnType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Definition</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Definition</em>' containment reference list.
   * @see eu.ddmore.pml.pharmML.PharmMLPackage#getct_DefinitionType_Definition()
   * @model containment="true"
   * @generated
   */
  EList<ct_ColumnType> getDefinition();

} // ct_DefinitionType
