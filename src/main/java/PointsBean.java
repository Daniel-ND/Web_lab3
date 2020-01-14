import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

@ManagedBean(name = "pointsBean")
@SessionScoped
public class PointsBean implements Serializable {
    private EntityManagerFactory entityManagerFactory;
    private Point newPoint = new Point();
    private Deque<Point> history;
    private String sessionID;

    public PointsBean() {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("NewPersistenceUnit");
        } catch (Exception e) {
            System.err.println("Initial SessionFactory creation failed. " + e);
        }
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        sessionID = session.getId();
        history = getSessionPoints(sessionID);
    }

    public void addPoint() {
        double x = newPoint.getX();
        double y = newPoint.getY();
        double r = newPoint.getR();
        if ((x >= 0 && y >= 0 && x <= r && y <= r/2) || (x >= 0 && y <= 0 && Math.pow(x, 2) + Math.pow(y, 2) <= Math.pow(r , 2))
                || (x <= 0 && y >= 0 && y - 2*x <= r )){
            newPoint.setResult(true);
        } else newPoint.setResult(false);

        newPoint.setSessionId(sessionID);

        if (entityManagerFactory != null) {
            try {
                EntityManager em = entityManagerFactory.createEntityManager();
                em.getTransaction().begin();
                em.persist(newPoint);
                em.getTransaction().commit();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        history.addFirst(newPoint);
        newPoint = new Point();
    }

    private LinkedList<Point> getSessionPoints(String sessionId) {
        LinkedList<Point> points = new LinkedList<Point>();
        if (entityManagerFactory != null) {
            try {
                EntityManager em = entityManagerFactory.createEntityManager();
                TypedQuery<Point> query = em.createQuery("from Point where sessionId = :id", Point.class);
                query.setParameter("id", sessionID);
                points = new LinkedList<Point>(query.getResultList());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return points;
    }


    public Point getNewPoint() {
        return newPoint;
    }

    public void setNewPoint(Point newPoint) {
        this.newPoint = newPoint;
    }

    public Deque<Point> getHistory() {
        return history;
    }

    public void setHistory(Deque<Point> history) {
        this.history = history;
    }
}
