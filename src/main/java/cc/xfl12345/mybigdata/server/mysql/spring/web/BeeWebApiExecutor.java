package cc.xfl12345.mybigdata.server.mysql.spring.web;

import cc.xfl12345.mybigdata.server.common.web.WebApiExecutor;
import cc.xfl12345.mybigdata.server.common.web.pojo.response.JsonApiResponseData;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.SessionFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.function.Function;

public class BeeWebApiExecutor extends WebApiExecutor {
    protected ThreadLocal<TransactionAndOkFlag> threadLocal;

    @Override
    public void init() throws Exception {
        super.init();
        threadLocal = new ThreadLocal<>();
    }

    @Override
    public <Param> JsonApiResponseData handle(HttpServletResponse httpServletResponse, Param param, Function<Param, Object> action) {
        Transaction transaction = SessionFactory.getTransaction();
        TransactionAndOkFlag transactionAndOkFlag = new TransactionAndOkFlag(transaction, true);
        threadLocal.set(transactionAndOkFlag);

        try {
            transaction.begin();
            JsonApiResponseData responseData = super.handle(httpServletResponse, param, action);
            if (transactionAndOkFlag.isOk()) {
                transaction.commit();
            }

            return responseData;
        } finally {
            threadLocal.remove();
        }
    }

    @Override
    protected <Param> void onError(HttpServletResponse httpServletResponse, Param param, JsonApiResponseData responseData, Exception exception) {
        TransactionAndOkFlag transactionAndOkFlag = threadLocal.get();
        transactionAndOkFlag.setOk(false);
        transactionAndOkFlag.getTransaction().rollback();
        super.onError(httpServletResponse, param, responseData, exception);
    }

    protected static class TransactionAndOkFlag {
        public TransactionAndOkFlag() {
        }

        public TransactionAndOkFlag(Transaction transaction, boolean ok) {
            this.transaction = transaction;
            this.ok = ok;
        }

        private Transaction transaction;

        private boolean ok;

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        public boolean isOk() {
            return ok;
        }

        public void setOk(boolean ok) {
            this.ok = ok;
        }
    }
}
