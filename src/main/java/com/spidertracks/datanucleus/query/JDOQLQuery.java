/**********************************************************************
Copyright (c) 2010 Todd Nine. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
    ...
 ***********************************************************************/
package com.spidertracks.datanucleus.query;


import java.util.Collection;
import java.util.Map;

import org.datanucleus.query.evaluator.JDOQLEvaluator;
import org.datanucleus.query.evaluator.JavaQueryEvaluator;
import org.datanucleus.store.ExecutionContext;
import org.datanucleus.store.query.AbstractJDOQLQuery;
import org.datanucleus.util.NucleusLogger;


/**
 * A query in JDOQL query language.
 *
 * @version $Id$
 */
public class JDOQLQuery extends AbstractJDOQLQuery
{
    /** Serialization number. */
    private static final long serialVersionUID = 2L;

    /** The type of query, used by the logger. */
    private static final String QUERY_TYPE = "JDOQL";

    /**
     * Constructs a new query instance that uses the given persistence manager.
     * 
     * @param ec the associated ExecutiongContext for this query.
     */
    public JDOQLQuery(final ExecutionContext ec) {
        this(ec, (JDOQLQuery) null);
    }

    /**
     * Constructs a new query instance having the same criteria as the given query.
     * 
     * @param ec The Executing Manager
     * @param q The query from which to copy criteria.
     */
    public JDOQLQuery(final ExecutionContext ec, final JDOQLQuery q) {
        super(ec, q);
    }

    /**
     * Constructor for a JDOQL query where the query is specified using the "Single-String" format.
     * 
     * @param ec The execution context
     * @param query The query string
     */
    public JDOQLQuery(final ExecutionContext ec, final String query) {
        super(ec, query);
    }

    @Override
    protected Object performExecute(Map parameters)
    {
        long startTime = System.currentTimeMillis();
        if (NucleusLogger.QUERY.isDebugEnabled()) {
            NucleusLogger.QUERY.debug(LOCALISER.msg("021046", QUERY_TYPE,
                getSingleStringQuery(), null));
        }

        final Object result =
            QueryHelper.executeQuery(parameters, this, new JDOQLQueryPostProcessor(this));

        if (NucleusLogger.QUERY.isDebugEnabled()) {
            NucleusLogger.QUERY.debug(LOCALISER.msg("021074", QUERY_TYPE, ""
                    + (System.currentTimeMillis() - startTime)));
        }

        return result;
    }

    /**
     * A postprocessor for JDOQL queries.
     */
    private static class JDOQLQueryPostProcessor implements QueryPostProcessor
    {
        /** The query to postprocess. */
        private final JDOQLQuery query;

        /**
         * The Constructor.
         *
         * @param query the query to postprocess.
         */
        public JDOQLQueryPostProcessor(final JDOQLQuery query)
        {
            this.query = query;
        }

        @Override
        public Collection<?> run(final Collection<?> candidates, final Map parameters)
        {
            final JavaQueryEvaluator evaluator =
                new JDOQLEvaluator(this.query,
                                   candidates,
                                   this.query.getCompilation(),
                                   parameters,
                                   query.getObjectManager().getClassLoaderResolver());

            return evaluator.execute(true, true, true, true, true);
        }
    }
}
