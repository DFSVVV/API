import {PageContainer} from '@ant-design/pro-components';
import {useModel} from '@umijs/max';
import {Avatar, Button, Card, Descriptions, Form, List, message, Skeleton, Spin, theme} from 'antd';
import React, {useCallback, useEffect, useState} from 'react';
import {list} from "postcss";
import {
  deleteInterfaceInfoUsingPost,
  getInterfaceInfoByIdUsingGet, invokeInterfaceInfoUsingPost,
  listInterfaceInfoByPageUsingGet
} from "@/services/lon/interfaceInfoController";
import {useMatch, useParams} from "@@/exports";
import TextArea from "antd/es/input/TextArea";
import {hide} from "@floating-ui/dom";

/**
 * 每个单独的卡片，为了复用样式抽成了组件
 * @param param0
 * @returns
 */
const InfoCard: React.FC<{
  title: string;
  index: number;
  desc: string;
  href: string;
}> = ({title, href, index, desc}) => {
  const {useToken} = theme;

  const {token} = useToken();

  return (
    <div
      style={{
        backgroundColor: token.colorBgContainer,
        boxShadow: token.boxShadow,
        borderRadius: '8px',
        fontSize: '14px',
        color: token.colorTextSecondary,
        lineHeight: '22px',
        padding: '16px 19px',
        minWidth: '220px',
        flex: 1,
      }}
    >
      <div
        style={{
          display: 'flex',
          gap: '4px',
          alignItems: 'center',
        }}
      >
        <div
          style={{
            width: 48,
            height: 48,
            lineHeight: '22px',
            backgroundSize: '100%',
            textAlign: 'center',
            padding: '8px 16px 16px 12px',
            color: '#FFF',
            fontWeight: 'bold',
            backgroundImage:
              "url('https://gw.alipayobjects.com/zos/bmw-prod/daaf8d50-8e6d-4251-905d-676a24ddfa12.svg')",
          }}
        >
          {index}
        </div>
        <div
          style={{
            fontSize: '16px',
            color: token.colorText,
            paddingBottom: 8,
          }}
        >
          {title}
        </div>
      </div>
      <div
        style={{
          fontSize: '14px',
          color: token.colorTextSecondary,
          textAlign: 'justify',
          lineHeight: '22px',
          marginBottom: 8,
        }}
      >
        {desc}
      </div>
      <a href={href} target="_blank" rel="noreferrer">
        了解更多 {'>'}
      </a>
    </div>
  );
};

const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<API.InterfaceInfo>();
  const [invoked, setInvoked] = useState<any>();
  const [invokedLoading, setInvokedLoading] = useState(false);
  // const match = useMatch('interface_info/:id');
  // alert(JSON.stringify(match, null, 4));
  const params = useParams();
  const loadData = async () => {
    if (!params.id) {
      message.error("参数不存在");
      return;
    }
    setLoading(true);
    try {
      const res = await getInterfaceInfoByIdUsingGet({
        id: Number(params.id)
      });
      setData(res.data);
    } catch (error: any) {
      message.error('请求失败,' + error.message);
    }
    setLoading(false);
  }
  useEffect(() => {
    loadData();
  }, []);
  const onFinish = async (values: any) => {
    if (!params.id) {
      message.error('接口不存在');
      return;
    }
    setInvokedLoading(true);
    try {
      const res = await invokeInterfaceInfoUsingPost({
        id: params.id,
        ...values,
      });
      setInvoked(res.data);
      message.success('调用成功');
    } catch (error: any) {
      hide();
      message.error('调用失败');
    }
    setInvokedLoading(false);
  };
  return (
    <PageContainer title="查看接口文档">
      <Card>
        {
          data ? (
            <Descriptions title={data.name} column={1}>
              <Descriptions.Item label="接口状态">{data.status ? '开启' : '关闭'}</Descriptions.Item>
              <Descriptions.Item label="描述">{data.description}</Descriptions.Item>
              <Descriptions.Item label="请求参数">{data.requestParams}</Descriptions.Item>
              <Descriptions.Item label="请求地址">{data.url}</Descriptions.Item>
              <Descriptions.Item label="请求方法">{data.method}</Descriptions.Item>
              <Descriptions.Item label="请求头">{data.requestHeader}</Descriptions.Item>
              <Descriptions.Item label="响应头">{data.responseHeader}</Descriptions.Item>
              <Descriptions.Item label="创建时间">{data.createTime}</Descriptions.Item>
              <Descriptions.Item label="更新时间">{data.updateTime}</Descriptions.Item>
            </Descriptions>
          ) : (
            <>接口不存在</>
          )
        }
      </Card>
      <Card>
        <Form
          name="invoke"
          // layout="vertical"
          onFinish={onFinish}

        >
          <Form.Item
            label="请求参数"
            name="userRequestParams"
          >
            <TextArea/>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              调用
            </Button>
          </Form.Item>
        </Form>
      </Card>
      <Card>
        <Spin spinning={invokedLoading}/>
        {invoked}
      </Card>
    </PageContainer>
  );
};

export default Index;
