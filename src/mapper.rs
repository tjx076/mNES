
pub mod mapper0;

pub trait IMapper {

    fn read(&self, adress: u16) -> u8;

    fn write(&mut self, address: u16, data: u8);
}