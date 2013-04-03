/**
 */
package org.ddmore.pml.pharmaML;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>msteps Simulation Step</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getId <em>Id</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getXmlns <em>Xmlns</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getDescription <em>Description</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getReplicates <em>Replicates</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getInitialValue <em>Initial Value</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getSimDataSet <em>Sim Data Set</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getObservations <em>Observations</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmsteps_SimulationStep()
 * @model
 * @generated
 */
public interface msteps_SimulationStep extends EObject
{
  /**
   * Returns the value of the '<em><b>Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Id</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id</em>' containment reference.
   * @see #setId(ct_id)
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmsteps_SimulationStep_Id()
   * @model containment="true"
   * @generated
   */
  ct_id getId();

  /**
   * Sets the value of the '{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getId <em>Id</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Id</em>' containment reference.
   * @see #getId()
   * @generated
   */
  void setId(ct_id value);

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
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmsteps_SimulationStep_Xmlns()
   * @model containment="true"
   * @generated
   */
  EList<ct_xmlns> getXmlns();

  /**
   * Returns the value of the '<em><b>Description</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Description</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Description</em>' containment reference.
   * @see #setDescription(ct_AnnotationType)
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmsteps_SimulationStep_Description()
   * @model containment="true"
   * @generated
   */
  ct_AnnotationType getDescription();

  /**
   * Sets the value of the '{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getDescription <em>Description</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Description</em>' containment reference.
   * @see #getDescription()
   * @generated
   */
  void setDescription(ct_AnnotationType value);

  /**
   * Returns the value of the '<em><b>Replicates</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Replicates</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Replicates</em>' containment reference.
   * @see #setReplicates(ct_ReplicatesType)
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmsteps_SimulationStep_Replicates()
   * @model containment="true"
   * @generated
   */
  ct_ReplicatesType getReplicates();

  /**
   * Sets the value of the '{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getReplicates <em>Replicates</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Replicates</em>' containment reference.
   * @see #getReplicates()
   * @generated
   */
  void setReplicates(ct_ReplicatesType value);

  /**
   * Returns the value of the '<em><b>Initial Value</b></em>' containment reference list.
   * The list contents are of type {@link org.ddmore.pml.pharmaML.msteps_InitialValueType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Initial Value</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Initial Value</em>' containment reference list.
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmsteps_SimulationStep_InitialValue()
   * @model containment="true"
   * @generated
   */
  EList<msteps_InitialValueType> getInitialValue();

  /**
   * Returns the value of the '<em><b>Sim Data Set</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Sim Data Set</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Sim Data Set</em>' containment reference.
   * @see #setSimDataSet(msteps_SimDataSetType)
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmsteps_SimulationStep_SimDataSet()
   * @model containment="true"
   * @generated
   */
  msteps_SimDataSetType getSimDataSet();

  /**
   * Sets the value of the '{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getSimDataSet <em>Sim Data Set</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Sim Data Set</em>' containment reference.
   * @see #getSimDataSet()
   * @generated
   */
  void setSimDataSet(msteps_SimDataSetType value);

  /**
   * Returns the value of the '<em><b>Observations</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Observations</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Observations</em>' containment reference.
   * @see #setObservations(msteps_ObservationsType)
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmsteps_SimulationStep_Observations()
   * @model containment="true"
   * @generated
   */
  msteps_ObservationsType getObservations();

  /**
   * Sets the value of the '{@link org.ddmore.pml.pharmaML.msteps_SimulationStep#getObservations <em>Observations</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Observations</em>' containment reference.
   * @see #getObservations()
   * @generated
   */
  void setObservations(msteps_ObservationsType value);

} // msteps_SimulationStep
