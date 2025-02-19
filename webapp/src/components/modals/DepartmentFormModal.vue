<template>
  <a-modal
    :open="open"
    :title="isEdit ? 'Изменение записи об отделе' : 'Создание записи об отделе'"
    @ok="handleOk"
    @cancel="handleCancel"
    okText="Сохранить"
    cancelText="Отмена"
  >
    <a-form ref="formRef" :model="formData" :rules="rules" layout="vertical">
      <a-form-item label="Родительский отдел" name="parent">
        <a-select v-model:value="formData.parent" placeholder="Не выбран">
          <a-select-option :value="null">
            <em>Не выбран</em>
          </a-select-option>
          <a-select-option
            v-for="parentDepartment in parentDepartments"
            :key="parentDepartment.id"
            :value="parentDepartment.id"
          >
            {{ parentDepartment.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="Наименование" name="name">
        <a-input v-model:value="formData.name" show-count :maxlength="255" />
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
  import { departmentApi } from '@/api/DepartmentApi'
  import type { UnwrapRef } from 'vue'
  import type { Rule } from 'ant-design-vue/es/form'
  import type { DepartmentItem } from '@/models/DepartmentItem'
  import type { DepartmentNameItem } from '@/models/DepartmentNameItem'
  import { AxiosError } from 'axios'

  interface FormState {
    parent: number | null
    name: string
    description: string | null
  }

  interface Props {
    open: boolean
    isEdit: boolean
    department: DepartmentItem | null
  }

  const props = defineProps<Props>()
  const emit = defineEmits<{
    (e: 'close'): void
    (e: 'submit', department: DepartmentItem | Omit<DepartmentItem, 'id'>): void
  }>()

  const formRef = ref()
  const parentDepartments = ref<DepartmentNameItem[]>([])

  const formData: UnwrapRef<FormState> = reactive({
    parent: null,
    name: '',
    description: null,
  })

  const uniqueError = ref(false)

  const rules: Record<string, Rule[]> = {
    name: [
      { required: true, message: 'Введите наименование', trigger: 'change' },
      {
        validator: () => {
          return new Promise((resolve, reject) => {
            if (uniqueError.value) {
              reject('Наименование уже используется')
            } else {
              resolve()
            }
          })
        },
        trigger: 'change',
      },
    ],
  }

  watch(
    () => props.department,
    async (newVal) => {
      if (newVal) {
        formData.name = newVal.name
        formData.description = newVal.description
        formData.parent = newVal.parent ?? null
        try {
          parentDepartments.value = await departmentApi.getAllowedParentList(newVal.id)
        } catch (error) {
          console.error('Ошибка загрузки родительских отделов:', error)
          message.error('Ошибка загрузки родительских отделов')
        }
      } else {
        formData.name = ''
        formData.description = null
        formData.parent = null
        try {
          parentDepartments.value = await departmentApi.getAllNames()
        } catch (error) {
          console.error('Ошибка загрузки родительских отделов:', error)
          message.error('Ошибка загрузки родительских отделов')
        }
      }
      uniqueError.value = false
    },
    { immediate: true }
  )

  watch(
    () => formData.name,
    () => {
      if (uniqueError.value) {
        uniqueError.value = false
        formRef.value?.validate()
      }
    }
  )

  const handleOk = async () => {
    try {
      await formRef.value.validate()
      formData.description = formData.description === '' ? null : formData.description
      let response: DepartmentItem
      const payload = { ...formData }
      if (props.isEdit && props.department) {
        response = await departmentApi.update({ ...props.department, ...payload })
      } else {
        response = await departmentApi.add(payload as Omit<DepartmentItem, 'id'>)
      }
      emit('submit', response)
    } catch (error) {
      if (error instanceof AxiosError) {
        if (error.response?.status === 409) {
          uniqueError.value = true
          formRef.value.validate()
        } else {
          console.error('Ошибка сохранения:', error)
          message.error('Ошибка сохранения')
        }
      }
    }
  }

  const handleCancel = () => {
    emit('close')
  }
</script>
