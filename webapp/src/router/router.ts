import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import ProfessionPage from "@/components/pages/ProfessionPage.vue";
import DepartmentPage from "@/components/pages/DepartmentPage.vue";
import EmployeePage from "@/components/pages/EmployeePage.vue";

const routes: RouteRecordRaw[] = [
  { path: "/profession", component: ProfessionPage },
  { path: "/department", component: DepartmentPage },
  { path: "/employee", component: EmployeePage },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
