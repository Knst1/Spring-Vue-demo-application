export interface DepartmentItem {
  id: number;
  parent: number | null;
  name: string;
  description: string | null;
  subDepartments: DepartmentItem[];
}
