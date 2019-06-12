package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.store.ProviderDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public abstract class HibernateProviderDao<T> implements ProviderDao<T> {

  private final Class<T> entityClass;

  public HibernateProviderDao(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  protected Class<T> getEntityClass() {
    return entityClass;
  }

  public abstract SessionFactory getSessionFactory();

  public abstract void setSessionFactory(SessionFactory sessionFactory);

  protected Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public T get(long id) throws IOException {
    return entityClass.cast(currentSession().get(entityClass, id));
  }

  @Override
  public List<T> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<T> results = currentSession().createCriteria(entityClass).list();
    return results;
  }

  protected <V> T getBy(String property, V value) {
    return entityClass.cast(currentSession().createCriteria(entityClass)
        .add(Restrictions.eq(property, value))
        .uniqueResult());
  }

  protected <U> long getUsageBy(Class<U> user, String property, T value) {
    return (long) currentSession().createCriteria(user)
        .add(Restrictions.eq(property, value))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
