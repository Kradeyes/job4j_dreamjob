package ru.job4j.dream.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.PsqlCandidateStore;
import ru.job4j.dream.store.PsqlPhotoStore;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class UploadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String candidateId = req.getParameter("candidate_id");
        RequestDispatcher dispatcher = req.getRequestDispatcher("/upload.jsp?candidate_id=" + candidateId);
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext context = this.getServletConfig().getServletContext();
        File repository = (File) context.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> items = upload.parseRequest(req);
            File folder = new File("images");
            if (!folder.exists()) {
                folder.mkdir();
            }
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    String newFileName = item.getName();
                    File file = new File(folder + File.separator + newFileName);
                    try (FileOutputStream output = new FileOutputStream(file)) {
                        output.write(item.getInputStream().readAllBytes());
                    }
                    int candidateId = Integer.parseInt(req.getParameter("candidate_id"));
                    Candidate candidate = PsqlCandidateStore.instOf().findCandidateById(candidateId);
                    int photoId = candidate.getPhotoId();
                    if (photoId > 1) {
                        PsqlPhotoStore.instOf().update(photoId, newFileName);
                    } else {
                        photoId = PsqlPhotoStore.instOf().add(newFileName);
                        PsqlCandidateStore.instOf().save(candidate.setPhotoId(photoId));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        req.setAttribute("candidates", PsqlCandidateStore.instOf().findAllCandidates());
        RequestDispatcher dispatcher = req.getRequestDispatcher("/candidates.jsp");
        dispatcher.forward(req, resp);
    }
}
