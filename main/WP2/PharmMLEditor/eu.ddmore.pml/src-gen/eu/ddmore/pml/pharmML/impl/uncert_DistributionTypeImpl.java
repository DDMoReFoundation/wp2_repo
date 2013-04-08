/**
 */
package eu.ddmore.pml.pharmML.impl;

import eu.ddmore.pml.pharmML.PharmMLPackage;
import eu.ddmore.pml.pharmML.XS_xmlns;
import eu.ddmore.pml.pharmML.ct_writtenVersion;
import eu.ddmore.pml.pharmML.uncert_DistributionType;
import eu.ddmore.pml.pharmML.uncert_DistributionTypeContent;

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
 * An implementation of the model object '<em><b>uncert Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link eu.ddmore.pml.pharmML.impl.uncert_DistributionTypeImpl#getXmlns <em>Xmlns</em>}</li>
 *   <li>{@link eu.ddmore.pml.pharmML.impl.uncert_DistributionTypeImpl#getWrittenVersion <em>Written Version</em>}</li>
 *   <li>{@link eu.ddmore.pml.pharmML.impl.uncert_DistributionTypeImpl#getExpr <em>Expr</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class uncert_DistributionTypeImpl extends MinimalEObjectImpl.Container implements uncert_DistributionType
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
   * The cached value of the '{@link #getWrittenVersion() <em>Written Version</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWrittenVersion()
   * @generated
   * @ordered
   */
  protected ct_writtenVersion writtenVersion;

  /**
   * The cached value of the '{@link #getExpr() <em>Expr</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExpr()
   * @generated
   * @ordered
   */
  protected uncert_DistributionTypeContent expr;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected uncert_DistributionTypeImpl()
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
    return PharmMLPackage.eINSTANCE.getuncert_DistributionType();
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
      xmlns = new EObjectContainmentEList<XS_xmlns>(XS_xmlns.class, this, PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__XMLNS);
    }
    return xmlns;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ct_writtenVersion getWrittenVersion()
  {
    return writtenVersion;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetWrittenVersion(ct_writtenVersion newWrittenVersion, NotificationChain msgs)
  {
    ct_writtenVersion oldWrittenVersion = writtenVersion;
    writtenVersion = newWrittenVersion;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__WRITTEN_VERSION, oldWrittenVersion, newWrittenVersion);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setWrittenVersion(ct_writtenVersion newWrittenVersion)
  {
    if (newWrittenVersion != writtenVersion)
    {
      NotificationChain msgs = null;
      if (writtenVersion != null)
        msgs = ((InternalEObject)writtenVersion).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__WRITTEN_VERSION, null, msgs);
      if (newWrittenVersion != null)
        msgs = ((InternalEObject)newWrittenVersion).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__WRITTEN_VERSION, null, msgs);
      msgs = basicSetWrittenVersion(newWrittenVersion, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__WRITTEN_VERSION, newWrittenVersion, newWrittenVersion));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public uncert_DistributionTypeContent getExpr()
  {
    return expr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetExpr(uncert_DistributionTypeContent newExpr, NotificationChain msgs)
  {
    uncert_DistributionTypeContent oldExpr = expr;
    expr = newExpr;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__EXPR, oldExpr, newExpr);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setExpr(uncert_DistributionTypeContent newExpr)
  {
    if (newExpr != expr)
    {
      NotificationChain msgs = null;
      if (expr != null)
        msgs = ((InternalEObject)expr).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__EXPR, null, msgs);
      if (newExpr != null)
        msgs = ((InternalEObject)newExpr).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__EXPR, null, msgs);
      msgs = basicSetExpr(newExpr, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__EXPR, newExpr, newExpr));
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
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__XMLNS:
        return ((InternalEList<?>)getXmlns()).basicRemove(otherEnd, msgs);
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__WRITTEN_VERSION:
        return basicSetWrittenVersion(null, msgs);
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__EXPR:
        return basicSetExpr(null, msgs);
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
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__XMLNS:
        return getXmlns();
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__WRITTEN_VERSION:
        return getWrittenVersion();
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__EXPR:
        return getExpr();
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
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__XMLNS:
        getXmlns().clear();
        getXmlns().addAll((Collection<? extends XS_xmlns>)newValue);
        return;
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__WRITTEN_VERSION:
        setWrittenVersion((ct_writtenVersion)newValue);
        return;
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__EXPR:
        setExpr((uncert_DistributionTypeContent)newValue);
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
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__XMLNS:
        getXmlns().clear();
        return;
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__WRITTEN_VERSION:
        setWrittenVersion((ct_writtenVersion)null);
        return;
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__EXPR:
        setExpr((uncert_DistributionTypeContent)null);
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
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__XMLNS:
        return xmlns != null && !xmlns.isEmpty();
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__WRITTEN_VERSION:
        return writtenVersion != null;
      case PharmMLPackage.UNCERT_DISTRIBUTION_TYPE__EXPR:
        return expr != null;
    }
    return super.eIsSet(featureID);
  }

} //uncert_DistributionTypeImpl