import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.dao.SimpleBlogDAO;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.*;

import static spark.Spark.*;


public class Main {

    public static void main(String[] args) {

        staticFileLocation("/public");

        // in memory data structure based DAO
        // switch out with database
        // just for prototyping
        BlogDao dao = new SimpleBlogDAO();
        fillBlogWithEntries(dao);

// ---- COOKIE ----
        before((request, response) -> {
            if (request.cookie("username") != null) {
                request.attribute("username", request.cookie("username"));
            }
        });


// ---- EDIT ----
        before(":slug/edit", (request, response) -> {
            String username = request.attribute("username");
            if (username == null || !username.equals("admin")) {
                String slug = request.params("slug");
                response.redirect("/password?slug=" + slug + "&redirectTo=edit");
                halt();
            }
        });

        get("/:slug/edit", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.findEntryBySlug(request.params("slug")));
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        post("/:slug/edit", (request, response) -> {
            BlogEntry entry = dao.findEntryBySlug(request.params("slug"));
            entry.setTitle(request.queryParams("title"));
            entry.setText(request.queryParams("text"));
            response.redirect("/" + entry.getSlug() + "/details");
            return null;
        });
// --------------

// ---- PASSWORD ----
        get("/password", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            String slug = request.queryParams("slug");
            model.put("slug", slug);
            String redirectTo = request.queryParams("redirectTo");
            model.put("redirectTo", redirectTo);
            System.out.println(slug);
            System.out.println(redirectTo);
            return new ModelAndView(model, "password.hbs");
        }, new HandlebarsTemplateEngine());

        post("/password", (request, response) -> {
            String username = request.queryParams("username");
            String slug = request.queryParams("slug");
            String redirectTo = request.queryParams("redirectTo");
            if (username.equals("admin")) {
                response.cookie("username", username);
                if (!slug.equals("")) {
                    response.redirect("/" + slug + "/" + redirectTo);
                } else {
                    response.redirect("/" + redirectTo);
                }
                halt();
            }
            response.redirect("/password");
            return null;
        });
// -----------------


// ---- DELETE -----
        before(":slug/delete", (request, response) -> {
            String username = request.attribute("username");
            if (username == null || !username.equals("admin")) {
                String slug = request.params("slug");
                response.redirect("/password?slug=" + slug + "&redirectTo=delete");
                halt();
            }
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
// ---------------


// ---- INDEX ------
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
// -----------------

       /* get("/details/index", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());*/

       /* get("/details/new", (request, response) -> {
            return new ModelAndView("", "new.hbs");
        }, new HandlebarsTemplateEngine());*/


        get("/:slug/details", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.findEntryBySlug(request.params("slug")));
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());


// ---- NEW ------
        before("/new", ((request, response) -> {
            String username = request.attribute("username");
            if (username == null || !username.equals("admin")) {
                response.redirect("/password?redirectTo=new");
                halt();
            }
        }));

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
// ---------------


// ---- COMMENT ------
        post("/:slug/add-comment", (request, response) -> {
            String commentAuthor = request.queryParams("name");
            String commentText = request.queryParams("comment");
            Comment comment = new Comment(commentAuthor, commentText);
            BlogEntry entry = dao.findEntryBySlug(request.params("slug"));
            entry.addComment(comment);
            response.redirect("/" + entry.getSlug() + "/details");
            return null;
        });


    }

    private static void fillBlogWithEntries(BlogDao dao) {
        HashSet<String> tags1 = new HashSet<>();
        tags1.add("yolo");
        tags1.add("tag2");
        //TODO:rm how can I escape the html tags like <tags1 ....> ?
        BlogEntry blogEntry = new BlogEntry(
                "Roman", "FML", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Nunc ut rhoncus felis, vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, " +
                "scelerisque sapien. Vivamus pharetra massa libero, sed feugiat turpis efficitur at.\n" +
                "Cras egestas ac ipsum in posuere. Fusce suscipit, libero id malesuada placerat, orci velit semper" +
                " metus, quis pulvinar sem nunc vel augue. In ornare tempor metus, sit amet congue justo porta et." +
                " Etiam pretium, sapien non fermentum consequat,gravida lacus, " +
                "non accumsan lorem odio id risus. Vestibulum pharetra tempor molestie. Integer sollicitudin ante" +
                " ipsum, tags1 luctus nisi egestas eu. Cras accumsan cursus ante, non dapibus tempor.",
                tags1
        );
        blogEntry.addComment(new Comment("Arthur", "this is my first comment."));
        dao.addEntry(blogEntry);

        HashSet<String> tags2 = new HashSet<>();
        tags2.add("cheesecake");
        tags2.add("food");
        dao.addEntry(new BlogEntry(
                "Doug Heffernan", "The Best Cheesecake", "This was the best cheesecake of my life.", tags2

        ));


        dao.addEntry(new BlogEntry(
                "Aleks", "How To Grow a Beard",
                "How to grow a beard... don't shave!"
        ));
    }


}
