/* Generated By:JJTree: Do not edit this line. OSelectStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ODatabaseRecordInternal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecordTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClassDescendentOrder;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLAsynchQuery;
import com.orientechnologies.orient.core.storage.OStorage;

public class OSelectStatement extends OStatement {

  protected OFromClause  target;

  protected OProjection  projection;

  protected OWhereClause whereClause;

  protected OGroupBy     groupBy;

  protected OOrderBy     orderBy;

  protected Integer      skip;

  protected Integer      limit;

  protected Boolean      lockRecord;

  protected OFetchPlan   fetchPlan;

  protected OLetClause   letClause;

  public OSelectStatement(int id) {
    super(id);
  }

  public OSelectStatement(OrientSql p, int id) {
    super(p, id);
  }

  public Object execute(OSQLAsynchQuery<ODocument> request, final Map<Object, Object> iArgs, OCommandContext context,
      OCommandResultListener listener, boolean sync) {
    return execute(request, iArgs, context, listener, -1, null, sync);
  }

  public Object execute(OSQLAsynchQuery<ODocument> request, final Map<Object, Object> iArgs, OCommandContext context,
      OCommandResultListener listener, long timeoutMs, OCommandContext.TIMEOUT_STRATEGY timeoutStrategy, boolean sync) {
    try {
      if (iArgs != null)
      // BIND ARGUMENTS INTO CONTEXT TO ACCESS FROM ANY POINT (EVEN FUNCTIONS)
      {
        for (Map.Entry<Object, Object> arg : iArgs.entrySet()) {
          context.setVariable(arg.getKey().toString(), arg.getValue());
        }
      }

      if (timeoutMs > 0) {
        context.beginExecution(timeoutMs, timeoutStrategy);
      }

      Iterator<? extends OIdentifiable> target = getTargetIterator(context, request.isUseCache());

      List<OIdentifiable> result = new ArrayList<OIdentifiable>();

      boolean canContinue = true;
      while (canContinue && target.hasNext()) {
        OIdentifiable targetResult = target.next();
        OIdentifiable projectionResult = this.calculateProjections(targetResult);
        if (this.matchesFilters(projectionResult)) {
          if (sync) {
            return result.add(projectionResult);
          } else {
            canContinue = listener.result(projectionResult);
          }
        }
        canContinue = canContinue && checkTimeout();
      }

      // if (!optimizeExecution()) {
      // fetchLimit = getQueryFetchLimit();
      //
      // executeSearch(iArgs);
      // applyExpand();
      // handleNoTarget();
      // handleGroupBy();
      // applyOrderBy();
      // applyLimitAndSkip();
      // }
      // return getResult();

      return null;
    } finally {
      if (listener != null) {
        listener.end();
      }
    }
  }

  private boolean checkTimeout() {
    return true;// TODO
  }

  public OIdentifiable calculateProjections(OIdentifiable result) {
    // TODO
    return result;
  }

  protected boolean matchesFilters(OIdentifiable currentRecord) {
    if(getWhereClause()==null){
      return true;
    }
    return getWhereClause().matchesFilters(currentRecord);
  }

  public Iterator<? extends OIdentifiable> getTargetIterator(OCommandContext iContext, boolean useCache) {

    if (target != null && target.getClassName() != null)
      return getClassTarget(iContext, useCache);
    // else if (parsedTarget.getTargetIndexValues() != null) {
    // target = new IndexValuesIterator(parsedTarget.getTargetIndexValues(), parsedTarget.isTargetIndexValuesAsc());
    // } else if (parsedTarget.getTargetClusters() != null)
    // searchInClusters();
    // else if (parsedTarget.getTargetRecords() != null) {
    // if (parsedTarget.getTargetRecords() instanceof OIterableRecordSource) {
    // target = ((OIterableRecordSource) parsedTarget.getTargetRecords()).iterator(iArgs);
    // } else {
    // target = parsedTarget.getTargetRecords().iterator();
    // }
    // } else if (parsedTarget.getTargetVariable() != null) {
    // final Object var = getContext().getVariable(parsedTarget.getTargetVariable());
    // if (var == null) {
    // target = Collections.EMPTY_LIST.iterator();
    // return true;
    // } else if (var instanceof OIdentifiable) {
    // final ArrayList<OIdentifiable> list = new ArrayList<OIdentifiable>();
    // list.add((OIdentifiable) var);
    // target = list.iterator();
    // } else if (var instanceof Iterable<?>)
    // target = ((Iterable<? extends OIdentifiable>) var).iterator();

    return null;
  }

  public Iterator<? extends OIdentifiable> getClassTarget(OCommandContext iContext, boolean useCache) {
    final ODatabaseRecordInternal database = new ODatabaseDocumentTx((ODatabaseRecordTx) getDatabase());

    String className = getTarget().getClassName().getValue();
    if (className == null) {
      return null;
    }

    OClass cls = database.getMetadata().getSchema().getClass(className);
    if (cls == null) {
      return null;// TODO throw exception;
    }
    database.checkSecurity(ODatabaseSecurityResources.CLASS, ORole.PERMISSION_READ, cls.getName().toLowerCase());

    // NO INDEXES: SCAN THE ENTIRE CLUSTER

    OStorage.LOCKING_STRATEGY locking = iContext != null && iContext.getVariable("$locking") != null ? (OStorage.LOCKING_STRATEGY) iContext
        .getVariable("$locking") : OStorage.LOCKING_STRATEGY.DEFAULT;

    // final ORID[] range = getRange();
    boolean ascendingOrder = true;//TODO check the query!
    if (ascendingOrder)
      return new ORecordIteratorClass<ORecord>(database, database, cls.getName(), true, useCache, false, locking);
    // .setRange(range[0], range[1]);
    else
      return new ORecordIteratorClassDescendentOrder<ORecord>(database, database, cls.getName(), true, useCache, false,
          locking);
    // .setRange(range[0], range[1]);}
  }

  protected void assignLetClauses(final ORecord iRecord) {

  }

  public OProjection getProjection() {
    return projection;
  }

  public void setProjection(OProjection projection) {
    this.projection = projection;
  }

  public OFromClause getTarget() {
    return target;
  }

  public void setTarget(OFromClause target) {
    this.target = target;
  }

  public OWhereClause getWhereClause() {
    return whereClause;
  }

  public void setWhereClause(OWhereClause whereClause) {
    this.whereClause = whereClause;
  }

  public OGroupBy getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(OGroupBy groupBy) {
    this.groupBy = groupBy;
  }

  public OOrderBy getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(OOrderBy orderBy) {
    this.orderBy = orderBy;
  }

  public Integer getSkip() {
    return skip;
  }

  public void setSkip(Integer skip) {
    this.skip = skip;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public Boolean getLockRecord() {
    return lockRecord;
  }

  public void setLockRecord(Boolean lockRecord) {
    this.lockRecord = lockRecord;
  }

  public OFetchPlan getFetchPlan() {
    return fetchPlan;
  }

  public void setFetchPlan(OFetchPlan fetchPlan) {
    this.fetchPlan = fetchPlan;
  }

  public OLetClause getLetClause() {
    return letClause;
  }

  public void setLetClause(OLetClause letClause) {
    this.letClause = letClause;
  }

  public static ODatabaseRecordInternal getDatabase() {
    return ODatabaseRecordThreadLocal.INSTANCE.get();
  }

}
/* JavaCC - OriginalChecksum=b26959b9726a8cf35d6283eca931da6b (do not edit this line) */
