import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    redirect:'/user',
    children:[
      {path: '/user', component: () => import('../views/user/UserView.vue')},
      {path: '/vehicle', component: () => import('../views/vehicle/VehicleView.vue')},
      {path: '/geofence', component: () => import('../views/geofence/GeofenceView.vue')},
      {path: '/geofenceMap', component: () => import('../views/geofence/GeofenceMapView.vue')},
      {path: '/application', component: () => import('../views/schedule/ApplicationView.vue')},
      {path: '/audit', component: () => import('../views/schedule/AuditView.vue')},
      {path: '/distribute', component: () => import('../views/schedule/DistributeView.vue')},
      {path: '/dict', component: () => import('../views/dictionary/DictView.vue')},
      {path: '/dictOption', component: () => import('../views/dictionary/DictOptionView.vue')}
    ]
  },
  {path: '/login', component: () => import('../views/LoginView')}
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router