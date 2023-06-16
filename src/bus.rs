
pub trait IBus {

    fn writeb(&mut self, address: u16, data: u8);

    fn writew(&mut self, address: u16, data: u16);

    fn readb(&self, address: u16) -> u8;

    fn readw(&self, address: u16) -> u16;

}

