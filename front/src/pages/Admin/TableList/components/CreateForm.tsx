import {ProColumns, ProTable,} from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';
import {Modal} from "antd";
import {FormValueType} from "@/pages/Admin/TableList/components/UpdateForm";

export type Props = {
  columns: ProColumns<API.InterfaceInfo>[];
  onCancel: () => void;
  onSubmit: (values: API.InterfaceInfo) => Promise<void>;
  visible: boolean;
};
const CreateForm: React.FC<Props> = (props) => {
  const {columns, visible, onCancel, onSubmit} = props;
  return <Modal visible={visible} onCancel={onCancel} footer={null}>
    <ProTable
      type="form"
      columns={columns}
      onSubmit={async (value) => {
        onSubmit?.(value);
      }}/>
  </Modal>;
};
export default CreateForm;
