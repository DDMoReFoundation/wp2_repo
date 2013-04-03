/**
 */
package org.ddmore.pml.pharmaML;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>msteps Dependant Step Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ddmore.pml.pharmaML.msteps_DependantStepType#getIdRef <em>Id Ref</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.msteps_DependantStepType#getXmlns <em>Xmlns</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmsteps_DependantStepType()
 * @model
 * @generated
 */
public interface msteps_DependantStepType extends EObject
{
  /**
   * Returns the value of the '<em><b>Id Ref</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Id Ref</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id Ref</em>' attribute.
   * @see #setIdRef(String)
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmsteps_DependantStepType_IdRef()
   * @model
   * @generated
   */
  String getIdRef();

  /**
   * Sets the value of the '{@link org.ddmore.pml.pharmaML.msteps_DependantStepType#getIdRef <em>Id Ref</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Id Ref</em>' attribute.
   * @see #getIdRef()
   * @generated
   */
  void setIdRef(String value);

  /**
   * Returns the value of the '<em><b>Xmlns</b></em>' containment reference list.
   * The list contents are of type {@link org.ddmore.pml.pharmaML.ct_xmlns}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Xmlns</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Xmlns</em>' containment reference list.
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmsteps_DependantStepType_Xmlns()
   * @model containment="true"
   * @generated
   */
  EList<ct_xmlns> getXmlns();

} // msteps_DependantStepType
