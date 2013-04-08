/**
 */
package eu.ddmore.pml.pharmML.impl;

import eu.ddmore.pml.pharmML.Math_VarType;
import eu.ddmore.pml.pharmML.PharmMLPackage;
import eu.ddmore.pml.pharmML.XS_xmlns;
import eu.ddmore.pml.pharmML.mdef_CovariateType;
import eu.ddmore.pml.pharmML.mdef_FixedEffectType;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>mdef Covariate Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link eu.ddmore.pml.pharmML.impl.mdef_CovariateTypeImpl#getXmlns <em>Xmlns</em>}</li>
 *   <li>{@link eu.ddmore.pml.pharmML.impl.mdef_CovariateTypeImpl#getVar <em>Var</em>}</li>
 *   <li>{@link eu.ddmore.pml.pharmML.impl.mdef_CovariateTypeImpl#getFixedEffect <em>Fixed Effect</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class mdef_CovariateTypeImpl extends MinimalEObjectImpl.Container implements mdef_CovariateType
{
  /**
   * The cached value of the '{@link #getXmlns() <em>Xmlns</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getXmlns()
   * @generated
   * @ordered
   */
  protected EList<XS_xmlns> xmlns;

  /**
   * The cached value of the '{@link #getVar() <em>Var</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVar()
   * @generated
   * @ordered
   */
  protected Math_VarType var;

  /**
   * The cached value of the '{@link #getFixedEffect() <em>Fixed Effect</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFixedEffect()
   * @generated
   * @ordered
   */
  protected EList<mdef_FixedEffectType> fixedEffect;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected mdef_CovariateTypeImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return PharmMLPackage.eINSTANCE.getmdef_CovariateType();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<XS_xmlns> getXmlns()
  {
    if (xmlns == null)
    {
      xmlns = new EObjectContainmentEList<XS_xmlns>(XS_xmlns.class, this, PharmMLPackage.MDEF_COVARIATE_TYPE__XMLNS);
    }
    return xmlns;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Math_VarType getVar()
  {
    return var;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetVar(Math_VarType newVar, NotificationChain msgs)
  {
    Math_VarType oldVar = var;
    var = newVar;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PharmMLPackage.MDEF_COVARIATE_TYPE__VAR, oldVar, newVar);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setVar(Math_VarType newVar)
  {
    if (newVar != var)
    {
      NotificationChain msgs = null;
      if (var != null)
        msgs = ((InternalEObject)var).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PharmMLPackage.MDEF_COVARIATE_TYPE__VAR, null, msgs);
      if (newVar != null)
        msgs = ((InternalEObject)newVar).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PharmMLPackage.MDEF_COVARIATE_TYPE__VAR, null, msgs);
      msgs = basicSetVar(newVar, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PharmMLPackage.MDEF_COVARIATE_TYPE__VAR, newVar, newVar));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<mdef_FixedEffectType> getFixedEffect()
  {
    if (fixedEffect == null)
    {
      fixedEffect = new EObjectContainmentEList<mdef_FixedEffectType>(mdef_FixedEffectType.class, this, PharmMLPackage.MDEF_COVARIATE_TYPE__FIXED_EFFECT);
    }
    return fixedEffect;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case PharmMLPackage.MDEF_COVARIATE_TYPE__XMLNS:
        return ((InternalEList<?>)getXmlns()).basicRemove(otherEnd, msgs);
      case PharmMLPackage.MDEF_COVARIATE_TYPE__VAR:
        return basicSetVar(null, msgs);
      case PharmMLPackage.MDEF_COVARIATE_TYPE__FIXED_EFFECT:
        return ((InternalEList<?>)getFixedEffect()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case PharmMLPackage.MDEF_COVARIATE_TYPE__XMLNS:
        return getXmlns();
      case PharmMLPackage.MDEF_COVARIATE_TYPE__VAR:
        return getVar();
      case PharmMLPackage.MDEF_COVARIATE_TYPE__FIXED_EFFECT:
        return getFixedEffect();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case PharmMLPackage.MDEF_COVARIATE_TYPE__XMLNS:
        getXmlns().clear();
        getXmlns().addAll((Collection<? extends XS_xmlns>)newValue);
        return;
      case PharmMLPackage.MDEF_COVARIATE_TYPE__VAR:
        setVar((Math_VarType)newValue);
        return;
      case PharmMLPackage.MDEF_COVARIATE_TYPE__FIXED_EFFECT:
        getFixedEffect().clear();
        getFixedEffect().addAll((Collection<? extends mdef_FixedEffectType>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case PharmMLPackage.MDEF_COVARIATE_TYPE__XMLNS:
        getXmlns().clear();
        return;
      case PharmMLPackage.MDEF_COVARIATE_TYPE__VAR:
        setVar((Math_VarType)null);
        return;
      case PharmMLPackage.MDEF_COVARIATE_TYPE__FIXED_EFFECT:
        getFixedEffect().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case PharmMLPackage.MDEF_COVARIATE_TYPE__XMLNS:
        return xmlns != null && !xmlns.isEmpty();
      case PharmMLPackage.MDEF_COVARIATE_TYPE__VAR:
        return var != null;
      case PharmMLPackage.MDEF_COVARIATE_TYPE__FIXED_EFFECT:
        return fixedEffect != null && !fixedEffect.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //mdef_CovariateTypeImpl