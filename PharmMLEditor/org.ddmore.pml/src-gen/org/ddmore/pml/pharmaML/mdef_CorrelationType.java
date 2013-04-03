/**
 */
package org.ddmore.pml.pharmaML;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>mdef Correlation Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ddmore.pml.pharmaML.mdef_CorrelationType#getLevelId <em>Level Id</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.mdef_CorrelationType#getXmlns <em>Xmlns</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.mdef_CorrelationType#getParamVar1 <em>Param Var1</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.mdef_CorrelationType#getParamVar2 <em>Param Var2</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.mdef_CorrelationType#getCorrelationCoefficient <em>Correlation Coefficient</em>}</li>
 *   <li>{@link org.ddmore.pml.pharmaML.mdef_CorrelationType#getCovariance <em>Covariance</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmdef_CorrelationType()
 * @model
 * @generated
 */
public interface mdef_CorrelationType extends EObject
{
  /**
   * Returns the value of the '<em><b>Level Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Level Id</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Level Id</em>' containment reference.
   * @see #setLevelId(ct_levelId)
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmdef_CorrelationType_LevelId()
   * @model containment="true"
   * @generated
   */
  ct_levelId getLevelId();

  /**
   * Sets the value of the '{@link org.ddmore.pml.pharmaML.mdef_CorrelationType#getLevelId <em>Level Id</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Level Id</em>' containment reference.
   * @see #getLevelId()
   * @generated
   */
  void setLevelId(ct_levelId value);

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
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmdef_CorrelationType_Xmlns()
   * @model containment="true"
   * @generated
   */
  EList<ct_xmlns> getXmlns();

  /**
   * Returns the value of the '<em><b>Param Var1</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Param Var1</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Param Var1</em>' containment reference.
   * @see #setParamVar1(Math_VarType)
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmdef_CorrelationType_ParamVar1()
   * @model containment="true"
   * @generated
   */
  Math_VarType getParamVar1();

  /**
   * Sets the value of the '{@link org.ddmore.pml.pharmaML.mdef_CorrelationType#getParamVar1 <em>Param Var1</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Param Var1</em>' containment reference.
   * @see #getParamVar1()
   * @generated
   */
  void setParamVar1(Math_VarType value);

  /**
   * Returns the value of the '<em><b>Param Var2</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Param Var2</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Param Var2</em>' containment reference.
   * @see #setParamVar2(Math_VarType)
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmdef_CorrelationType_ParamVar2()
   * @model containment="true"
   * @generated
   */
  Math_VarType getParamVar2();

  /**
   * Sets the value of the '{@link org.ddmore.pml.pharmaML.mdef_CorrelationType#getParamVar2 <em>Param Var2</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Param Var2</em>' containment reference.
   * @see #getParamVar2()
   * @generated
   */
  void setParamVar2(Math_VarType value);

  /**
   * Returns the value of the '<em><b>Correlation Coefficient</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Correlation Coefficient</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Correlation Coefficient</em>' containment reference.
   * @see #setCorrelationCoefficient(mdef_CorrelationCoefficientType)
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmdef_CorrelationType_CorrelationCoefficient()
   * @model containment="true"
   * @generated
   */
  mdef_CorrelationCoefficientType getCorrelationCoefficient();

  /**
   * Sets the value of the '{@link org.ddmore.pml.pharmaML.mdef_CorrelationType#getCorrelationCoefficient <em>Correlation Coefficient</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Correlation Coefficient</em>' containment reference.
   * @see #getCorrelationCoefficient()
   * @generated
   */
  void setCorrelationCoefficient(mdef_CorrelationCoefficientType value);

  /**
   * Returns the value of the '<em><b>Covariance</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Covariance</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Covariance</em>' containment reference.
   * @see #setCovariance(mdefCovarianceType)
   * @see org.ddmore.pml.pharmaML.PharmaMLPackage#getmdef_CorrelationType_Covariance()
   * @model containment="true"
   * @generated
   */
  mdefCovarianceType getCovariance();

  /**
   * Sets the value of the '{@link org.ddmore.pml.pharmaML.mdef_CorrelationType#getCovariance <em>Covariance</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Covariance</em>' containment reference.
   * @see #getCovariance()
   * @generated
   */
  void setCovariance(mdefCovarianceType value);

} // mdef_CorrelationType
