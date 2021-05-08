package ru.job4j.dream.servlet;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.PsqlCandidateStore;
import ru.job4j.dream.store.PsqlPhotoStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CandidateDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Candidate can = PsqlCandidateStore.instOf().findCandidateById(id);
        PsqlCandidateStore.instOf().delete(id);
        if (can.getPhotoId() != 1) {
            PsqlPhotoStore.instOf().delete(can.getPhotoId());
        }
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
