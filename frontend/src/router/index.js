import { createRouter, createWebHashHistory } from 'vue-router';

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      component: () => import('../components/pages/Index.vue'),
    },
    {
      path: '/users',
      component: () => import('../components/ui/UserGrid.vue'),
    },
    {
      path: '/authors',
      component: () => import('../components/ui/AuthorGrid.vue'),
    },
    {
      path: '/publicationStatuses',
      component: () => import('../components/PublicationStatusView.vue'),
    },
    {
      path: '/bookWorks',
      component: () => import('../components/ui/BookWorkGrid.vue'),
    },
    {
      path: '/books',
      component: () => import('../components/ui/BookGrid.vue'),
    },
    {
      path: '/bookViews',
      component: () => import('../components/ui/BookViewGrid.vue'),
    },
    {
      path: '/manuscripts',
      component: () => import('../components/ui/ManuscriptGrid.vue'),
    },
    {
      path: '/manuscriptLists',
      component: () => import('../components/ManuscriptListView.vue'),
    },
    {
      path: '/points',
      component: () => import('../components/ui/PointGrid.vue'),
    },
    {
      path: '/pointInquiries',
      component: () => import('../components/PointInquiryView.vue'),
    },
  ],
})

export default router;
