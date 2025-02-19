<template>
  <a-modal
    :open="open"
    :title="isEdit ? 'Изменение записи о сотруднике' : 'Создание записи о сотруднике'"
    @ok="handleOk"
    @cancel="handleCancel"
    okText="Сохранить"
    cancelText="Отмена"
  >
    <a-form ref="formRef" :model="formData" :rules="rules" layout="vertical">
      <a-form-item label="ФИО" name="name">
        <a-input v-model:value="formData.name" show-count :maxlength="255" />
      </a-form-item>

      <a-form-item label="Профессия" name="profession">
        <a-select v-model:value="formData.profession" placeholder="Не выбрана">
          <a-select-option
            v-for="profession in professions"
            :key="profession.id"
            :value="profession.id"
          >
            {{ profession.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="Отдел" name="department">
        <a-select v-model:value="formData.department" placeholder="Не выбран">
          <a-select-option
            v-for="department in departments"
            :key="department.id"
            :value="department.id"
          >
            {{ department.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="Примечание" name="description">
        <a-textarea v-model:value="formData.description" show-count :maxlength="5000" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
  import { reactive, watch, ref } from 'vue'
  import { message } from 'ant-design-vue'
  import { employeeApi } from '@/api/EmployeeApi'
  import { professionApi } from '@/api/ProfessionApi'
  import { departmentApi } from '@/api/DepartmentApi'
  import { AxiosError } from 'axios'
  import type { UnwrapRef } from 'vue'
  import type { Rule } from 'ant-design-vue/es/form'
  import type { EmployeeItem } from '@/models/EmployeeItem'
  import type { DepartmentNameItem } from '@/models/DepartmentNameItem'
  import type { ProfessionNameItem } from '@/models/ProfessionNameItem'

  interface FormState {
    name: string
    department: number | null
    profession: number | null
    description: string | null
  }

  interface Props {
    open: boolean
    isEdit: boolean
    employee: EmployeeItem | null
  }

  const props = defineProps<Props>()
  const emit = defineEmits<{
    (e: 'close'): void
    (e: 'submit', employee: EmployeeItem): void
  }>()

  const formRef = ref()
  const departments = ref<DepartmentNameItem[]>([])
  const professions = ref<ProfessionNameItem[]>([])

  const formData: UnwrapRef<FormState> = reactive({
    name: '',
    department: null,
    profession: null,
    description: null,
  })

  const rules: Record<string, Rule[]> = {
    name: [{ required: true, message: 'Введите ФИО', trigger: 'change' }],
    profession: [{ required: true, message: 'Выберите профессию', trigger: 'change' }],
    department: [{ required: true, message: 'Выберите отдел', trigger: 'change' }],
  }

  watch(
    () => props.employee,
    async (newVal) => {
      try {
        departments.value = await departmentApi.getAllNames()
      } catch (error) {
        if (error instanceof AxiosError) {
          console.error('Ошибка загрузки отделов:', error)
          message.error('Ошибка загрузки отделов')
        }
      }
      try {
        professions.value = await professionApi.getAllNames()
      } catch (error) {
        if (error instanceof AxiosError) {
          console.error('Ошибка загрузки профессий:', error)
          message.error('Ошибка загрузки профессий')
        }
      }
      if (newVal) {
        formData.name = newVal.name
        formData.department = newVal.department
        formData.profession = newVal.profession
        formData.description = newVal.description
      } else {
        formData.name = ''
        formData.department = null
        formData.profession = null
        formData.description = null
      }
    },
    { immediate: true }
  )

  const handleOk = async () => {
    try {
      await formRef.value.validate()
      formData.description = formData.description === '' ? null : formData.description
      let response: EmployeeItem
      const payload = {
        ...formData,
        department: formData.department as number,
        profession: formData.profession as number,
      }
      if (props.isEdit && props.employee) {
        response = await employeeApi.update({ ...props.employee, ...payload })
      } else {
        response = await employeeApi.add({ ...payload })
      }
      emit('submit', response)
    } catch (error) {
      if (error instanceof AxiosError) {
        console.error('Ошибка сохранения:', error)
        message.error('Ошибка сохранения')
      }
    }
  }

  const handleCancel = () => {
    emit('close')
  }
</script>
