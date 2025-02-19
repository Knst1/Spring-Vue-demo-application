<template>
  <a-modal
    :open="open"
    :title="isEdit ? 'Изменение записи о профессии' : 'Создание записи о профессии'"
    @ok="handleOk"
    @cancel="handleCancel"
    okText="Сохранить"
    cancelText="Отмена"
  >
    <a-form ref="formRef" :model="formData" :rules="rules" layout="vertical">
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
  import { professionApi } from '@/api/ProfessionApi'
  import { AxiosError } from 'axios'
  import type { UnwrapRef } from 'vue'
  import type { Rule } from 'ant-design-vue/es/form'
  import type { ProfessionItem } from '@/models/ProfessionItem'

  interface FormState {
    name: string
    description: string | null
  }

  interface Props {
    open: boolean
    isEdit: boolean
    profession: ProfessionItem | null
  }

  const props = defineProps<Props>()
  const emit = defineEmits<{
    (e: 'close'): void
    (e: 'submit', profession: ProfessionItem): void
  }>()

  const formRef = ref()

  const formData: UnwrapRef<FormState> = reactive({
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
    () => props.profession,
    (newVal) => {
      if (newVal) {
        formData.name = newVal.name
        formData.description = newVal.description
      } else {
        formData.name = ''
        formData.description = null
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
      let response: ProfessionItem
      if (props.isEdit && props.profession) {
        response = await professionApi.update({ ...props.profession, ...formData })
      } else {
        response = await professionApi.add({ ...formData })
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
