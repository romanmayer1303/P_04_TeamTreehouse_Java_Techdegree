import com.sun.org.apache.xpath.internal.operations.Mod;
import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.dao.SimpleBlogDAO;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;


public class Main {

    private static final String FLASH_MESSAGE_KEY = "flash_message";

    public static void main(String[] args) {

        staticFileLocation("/public");
//        staticFileLocation("/css");

        // in memory data structure based DAO
        // switch out with database
        // just for prototyping
        BlogDao dao = new SimpleBlogDAO();

        //TODO:rm how can I escape the html tags like <a ....> ?
        BlogEntry blogEntry = new BlogEntry(
                "Roman", "FML", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Nunc ut rhoncus felis, vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, " +
                "scelerisque sapien. Vivamus pharetra massa libero, sed feugiat turpis efficitur at.\n" +
                "Cras egestas ac ipsum in posuere. Fusce suscipit, libero id malesuada placerat, orci velit semper" +
                " metus, quis pulvinar sem nunc vel augue. In ornare tempor metus, sit amet congue justo porta et." +
                " Etiam pretium, sapien non fermentum consequat, \\<a href=\"\">dolor augue\\</a\\> gravida lacus, " +
                "non accumsan lorem odio id risus. Vestibulum pharetra tempor molestie. Integer sollicitudin ante" +
                " ipsum, a luctus nisi egestas eu. Cras accumsan cursus ante, non dapibus tempor."
        );
        blogEntry.addComment(new Comment("Arthur", "this is my first comment."));
        dao.addEntry(blogEntry);
        dao.addEntry(new BlogEntry(
                "Doug Heffernan", "The Best Cheesecake", "yolo"
        ));
        dao.addEntry(new BlogEntry(
                "Aleks", "How To ...",
                "asdfas;dfasdfasdfasdfasdf"
        ));

        before((request, response) -> {
            if (request.cookie("admin") != null) {
                request.attribute("admin", request.cookie("admin"));
            }
        });

        before("/new", ((request, response) -> {
            if (request.attribute("admin") == null) {
                setFlashMessage(request, "Whoops, please sign in first.");
                response.redirect("/password");
                halt();
            }
        }));

        before(":slug/edit", (request, response) -> {
            if (request.attribute("admin") == null) {
                setFlashMessage(request, "Whoops, please sign in first.");
                response.redirect("/password");
                halt();
            }
        });

        before(":slug/delete", (request, response) -> {
            if (request.attribute("admin") == null) {
                setFlashMessage(request, "Whoops, please sign in first.");
                response.redirect("/password");
                halt();
            }
        });

        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get("/index", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get("/details/index", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get("/details/new", (request, response) -> {
            return new ModelAndView("", "new.hbs");
        }, new HandlebarsTemplateEngine());

        get("/details/:slug", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.findEntryBySlug(request.params("slug")));
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/:slug/edit", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.findEntryBySlug(request.params("slug")));
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        post("/:slug/edit", (request, response) -> {
            BlogEntry entry = dao.findEntryBySlug(request.params("slug"));
            entry.setTitle(request.queryParams("title"));
            entry.setText(request.queryParams("text"));
            response.redirect("/details/" + entry.getSlug());
            return null;
        });

        get("/:slug/delete", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.findEntryBySlug(request.params("slug")));
            return new ModelAndView(model, "delete.hbs");
        }, new HandlebarsTemplateEngine());

        post("/:slug/delete", (request, response) -> {
            BlogEntry entry = dao.findEntryBySlug(request.params("slug"));
            dao.deleteEntry(entry);
            response.redirect("/");
            return null;
        });

        get("/password", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "password.hbs");
        }, new HandlebarsTemplateEngine());

        post("/sign-in", (request, response) -> {
            Map<String, String> model = new HashMap<>();
            String password = request.queryParams("password");
            response.cookie("admin", password);
            model.put("admin", password);
            response.redirect("/");
            return null;
        });

        get("/new", (request, response) -> {
            return new ModelAndView("", "new.hbs");
        }, new HandlebarsTemplateEngine());

        post("/new", (request, response) -> {
            String author = "admin";
            String title = request.queryParams("title");
            String text = request.queryParams("entry");
            dao.addEntry(new BlogEntry(author, title, text));
            response.redirect("/");
            return null;
        });

        post("/:slug/add-comment" ,(request, response) -> {
            String commentAuthor = request.queryParams("name");
            String commentText = request.queryParams("comment");
            Comment comment = new Comment(commentAuthor, commentText);
            BlogEntry entry = dao.findEntryBySlug(request.params("slug"));
            entry.addComment(comment);
            response.redirect("/details/" + entry.getSlug());
            return null;
        });



    }

    private static void setFlashMessage(Request request, String message) {
        request.session().attribute(FLASH_MESSAGE_KEY, message);
    }


}
