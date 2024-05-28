import {ProColumns, ProTable,} from '@ant-design/pro-components';
import '@umijs/max';
import React, {useEffect, useRef} from 'react';
import {Modal} from "antd";
import {FormValueType} from "@/pages/Admin/TableList/components/UpdateForm";

export type Props = {
  values: API.InterfaceInfo;
  columns: ProColumns<API.InterfaceInfo>[];
  onCancel: () => void;
  onSubmit: (values: API.InterfaceInfo) => Promise<void>;
  visible: boolean;
};
const UpdateForm: React.FC<Props> = (props) => {
  const {values, columns, visible, onCancel, onSubmit} = props;
  const formRef = useRef<ProFromInstance>();
  useEffect(() => {
    if (formRef) {
      formRef.current?.setFieldsValue(values);
    }
  }, [values])
  return <Modal visible={visible} footer={null} onCancel={onCancel}>
    <ProTable
      type="form"
      columns={columns}
      formRef={formRef}
      onSubmit={async (value) => {
        onSubmit?.(value);
      }}/>
  </Modal>;
};
export default UpdateForm;
