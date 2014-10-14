/**
 */
package eu.ddmore.pml.pharmML;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Math Piecewise Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link eu.ddmore.pml.pharmML.Math_PiecewiseType#getXmlns <em>Xmlns</em>}</li>
 *   <li>{@link eu.ddmore.pml.pharmML.Math_PiecewiseType#getPiecewise <em>Piecewise</em>}</li>
 * </ul>
 * </p>
 *
 * @see eu.ddmore.pml.pharmML.PharmMLPackage#getMath_PiecewiseType()
 * @model
 * @generated
 */
public interface Math_PiecewiseType extends EObject
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
   * @see eu.ddmore.pml.pharmML.PharmMLPackage#getMath_PiecewiseType_Xmlns()
   * @model containment="true"
   * @generated
   */
  EList<XS_xmlns> getXmlns();

  /**
   * Returns the value of the '<em><b>Piecewise</b></em>' containment reference list.
   * The list contents are of type {@link eu.ddmore.pml.pharmML.Math_PieceType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Piecewise</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Piecewise</em>' containment reference list.
   * @see eu.ddmore.pml.pharmML.PharmMLPackage#getMath_PiecewiseType_Piecewise()
   * @model containment="true"
   * @generated
   */
  EList<Math_PieceType> getPiecewise();

} // Math_PiecewiseType
