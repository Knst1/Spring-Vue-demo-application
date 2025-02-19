import apiClient from './ApiClient';
import type { EmployeeItem } from '@/models/EmployeeItem';

const API_URL = 'employees';

export const employeeApi = {
  async getAll(): Promise<EmployeeItem[]> {
    const response = await apiClient.get<EmployeeItem[]>(API_URL);
    return response.data;
  },

  async add(item: Omit<EmployeeItem, 'id'>): Promise<EmployeeItem> {
    const response = await apiClient.post<EmployeeItem>(API_URL, item);
    return response.data;
  },

  async update(item: EmployeeItem): Promise<EmployeeItem> {
    const response = await apiClient.patch<EmployeeItem>(`${API_URL}/${item.id}`, item);
    return response.data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(`${API_URL}/${id}`);
  }
};
